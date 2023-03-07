package ch.zhaw.babynames.controller;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.el.stream.Optional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.opencsv.CSVReader;
import ch.zhaw.babynames.model.Name;

@RestController
public class NameController {
    private ArrayList<Name> listOfNames;

    @GetMapping("/names")
    public ArrayList<Name> getNames() {
        return listOfNames;
    }

    @GetMapping("/names/count")
    public long getCount(@RequestParam(required = false) String sex) {
        long count = listOfNames.size();
        if (sex != null) {
            return listOfNames.stream().filter(x -> sex.equalsIgnoreCase(x.getGeschlecht())).count();
        } else {
            return count;
        }
    }

    @GetMapping("/names/frequency")
    public long getFrequency(@RequestParam String name) {
        Name matchingObject = listOfNames.stream().filter(x -> x.getName().equals(name)).findAny().orElse(null);
        if (matchingObject == null) {
            return 0;
        } else {
            return matchingObject.getAnzahl();
        }
    }  
    
    @GetMapping("/names/name")
    public ResponseEntity <List<String>> filterNames(@RequestParam String sex, @RequestParam String start,
            @RequestParam int length) {
        List<String> names = listOfNames.stream()
                .filter(x -> x.getName().toLowerCase().startsWith(start.toLowerCase()))
                .filter(x -> x.getName().length() == length)
                .filter(x -> x.getGeschlecht().equals(sex))
                .map(x -> x.getName())
                .collect(Collectors.toList());
        if ("w".equals(sex) || "m".equals(sex)) {
            return new ResponseEntity<>(names, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() throws Exception {
        listOfNames = new ArrayList<>();
        Path path = Paths.get(ClassLoader.getSystemResource("data/babynames.csv").toURI());
        System.out.println("Read from: " + path);
        try (Reader reader = Files.newBufferedReader(path)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    listOfNames.add(new Name(line[0], line[1], Integer.parseInt(line[2])));
                }
                System.out.println("Read " + listOfNames.size() + " names");
            }
        }
    }
}
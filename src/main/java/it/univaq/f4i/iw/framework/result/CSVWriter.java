package it.univaq.f4i.iw.framework.result;

import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CSVWriter {

    //Directory "relativa" del file in cui caricare gli eventi relativi a un corso
    private static final String CSV_CORSO_FILE = "csv\\eventi_corso.csv";
    //Directory "relativa" del file in cui caricare la configurazione dei gruppi
    private static final String CSV_GRUPPI_FILE = "csv\\gruppi.csv";
    //Directory "relativa" del file in cui caricare gli eventi relativi ad un intervallo temporale
    private static final String CSV_TIME_FILE = "csv\\eventi_time.csv";
    
    public void csv_time(List<Evento> eventi, String path) throws IOException {
        //Unione della directory del contesto con la directory relativa --> PATH esatto del file
        String dir = path + CSV_TIME_FILE;
        
        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(dir));){
            //Cambio del delimitatore
            CSVFormat csvFormat = CSVFormat.RFC4180.builder().
                    setDelimiter(';').setHeader(EventiHeader.class).build();
            
            try( CSVPrinter csvPrinter = new CSVPrinter(
                    writer, csvFormat
            );){
                for (Evento evento : eventi) {
                    csvPrinter.printRecord(
                            evento.getDataInizio(),
                            evento.getDataFine(),
                            evento.getNome(),
                            evento.getDescrizione(),
                            evento.getEmailResponsabile(),
                            evento.getAula().getNome(),
                            evento.getAula().getEdificio(),
                            evento.getAula().getLuogo(),
                            evento.getTipologia(),
                            evento.getNomeCorso(),
                            evento.getTipologiaRicorrenza(),
                            evento.getDataFineRicorrenza()
                    );
                }

                csvPrinter.flush();
            }
        }
    }
    
    public void csv_gruppi(List<List<String>> confs, String path) throws IOException{
        //Unione della directory del contesto con la directory relativa --> PATH esatto del file
        String dir = path + CSV_GRUPPI_FILE;
        
        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(dir));){
            
            //Cambio del delimitatore
            CSVFormat csvFormat = CSVFormat.RFC4180.builder().
                    setDelimiter(';').setHeader(GruppiHeader.class).build();
            
            try( CSVPrinter csvPrinter = new CSVPrinter(
                    writer, csvFormat
            );){
                for(List<String> conf : confs){
                    csvPrinter.printRecord(conf.get(0),
                    conf.get(1),
                    conf.get(2),
                    conf.get(3),
                    conf.get(4)
                    );
                }
                csvPrinter.flush();
            }

        }
    }

    public void csv_corso(List<Evento> eventi, String path) throws IOException {

        //Unione della directory del contesto con la directory relativa --> PATH esatto del file
        String dir = path + CSV_CORSO_FILE;

        try (
                 BufferedWriter writer = Files.newBufferedWriter(Paths.get(dir));) {

            //Cambio del delimitatore
            CSVFormat csvFormat = CSVFormat.RFC4180.builder().
                    setDelimiter(';').setHeader(EventiHeader.class).build();

            try ( CSVPrinter csvPrinter = new CSVPrinter(
                    writer, csvFormat
            );) {

                for (Evento evento : eventi) {
                    csvPrinter.printRecord(
                            evento.getDataInizio(),
                            evento.getDataFine(),
                            evento.getNome(),
                            evento.getDescrizione(),
                            evento.getEmailResponsabile(),
                            evento.getAula().getNome(),
                            evento.getAula().getEdificio(),
                            evento.getAula().getLuogo(),
                            evento.getTipologia(),
                            evento.getNomeCorso(),
                            evento.getTipologiaRicorrenza(),
                            evento.getDataFineRicorrenza()
                                
                    );
                }

                csvPrinter.flush();
            }

        }
    }

}

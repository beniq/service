package lerrain.service.printer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:spring.xml")
public class PrinterApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(PrinterApplication.class, args);
    }
}

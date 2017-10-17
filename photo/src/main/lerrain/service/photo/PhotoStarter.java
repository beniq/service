package lerrain.service.photo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:spring.xml")
public class PhotoStarter
{
    public static void main(String[] args)
    {
        SpringApplication.run(PhotoStarter.class, args);
    }
}


package com.jphaugla.boot;

import com.jphaugla.domain.Customer;
import com.redislabs.lettusearch.CreateOptions;
import com.redislabs.lettusearch.Field;
import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import io.lettuce.core.RedisCommandExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(6)
@Slf4j
public class CustomerIndex implements CommandLineRunner {

  @Autowired
  private StatefulRediSearchConnection<String, String> customerSearchConnection;

  @Value("${app.customerSearchIndexName}")
  private String customerSearchIndexName;

  @Override
  @SuppressWarnings({ "unchecked" })
  public void run(String... args) throws Exception {
    RediSearchCommands<String, String> commands = customerSearchConnection.sync();
    try {
      commands.ftInfo(customerSearchIndexName);
    } catch (RedisCommandExecutionException rcee) {
      if (rcee.getMessage().equals("Unknown Index name")) {

        CreateOptions<String, String> options = CreateOptions.<String, String>builder()//
            .prefix(customerSearchIndexName + ':').build();

        Field<String> city = Field.text("city").build();
        Field<String> firstName = Field.text("firstName").build();
        Field<String> fullName = Field.text("fullName").build();
        Field<String> lastName = Field.text("lastName").build();
        Field<String> stateAbbreviation = Field.text("stateAbbreviation").build();
        Field<String> zipcode = Field.text("zipcode").build();

        commands.create(
          customerSearchIndexName, //
          options, //
                city, firstName, fullName, lastName, stateAbbreviation, zipcode
        );

        log.info(">>>> Created " + customerSearchIndexName + " Search Index...");
      }
    }
  }

}

package com.jphaugla.boot;

import com.jphaugla.domain.Transaction;
import com.redislabs.lettusearch.CreateOptions;
import com.redislabs.lettusearch.Field;
import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.StatefulRediSearchConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.lettuce.core.RedisCommandExecutionException;


import lombok.extern.slf4j.Slf4j;

@Component
@Order(6)
@Slf4j
public class TransactionIndex implements CommandLineRunner {

  @Autowired
  private StatefulRediSearchConnection<String, String> transactionSearchConnection;

  @Value("${app.transactionSearchIndexName}")
  private String transactionSearchIndexName;

  @Override
  @SuppressWarnings({ "unchecked" })
  public void run(String... args) throws Exception {
    RediSearchCommands<String, String> commands = transactionSearchConnection.sync();
    try {
      commands.ftInfo(transactionSearchIndexName);
    } catch (RedisCommandExecutionException rcee) {
      if (rcee.getMessage().equals("Unknown Index name")) {

        CreateOptions<String, String> options = CreateOptions.<String, String>builder()//
            .prefix(transactionSearchIndexName + ':').build();

        Field<String> accountNo = Field.text("accountNo").build();
        Field<String> merchantAccount = Field.text("merchantAccount").build();
        Field<String> status = Field.text("status").build();
        Field<String> transactionReturn = Field.text("transactionReturn").build();

        commands.create(
          transactionSearchIndexName, //
          options, //
                accountNo, merchantAccount, status, transactionReturn
        );

        log.info(">>>> Created " + transactionSearchIndexName + " Search Index...");
      }
    }
  }

}

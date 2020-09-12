package com.sftp.SftpServerConnection;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.RegexPatternFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.messaging.MessageHandler;


@EnableIntegration
@IntegrationComponentScan
@SpringBootApplication
public class IntegrationFlowsPractice {

	public static void main(String[] args) {
		SpringApplication.run(IntegrationFlowsPractice.class, args);
	}


	@Bean
	public IntegrationFlow upcaseFlow() {
		return IntegrationFlows.from(sourceDirectory(), 
				c -> c.poller(Pollers.fixedDelay(1000))).transform(fileToStringTransformer())
				.handle(targetDirectory()).get();
		
	}

	@Bean
	public MessageSource<File> sourceDirectory() {
		CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
		filters.addFilter(new SimplePatternFileListFilter(".txt"));
		//filters.addFilter(new RegexPatternFileListFilter(""));
		
		FileReadingMessageSource messageSource = new FileReadingMessageSource();
		messageSource.setDirectory(new File("C:\\Users\\craig\\OneDrive"));
		messageSource.setFilter(filters);
		return messageSource;
	}

	@Bean
	public GenericSelector<File> onlyTxt() {
		return new GenericSelector<File>() {

			@Override
			public boolean accept(File source) {
				return source.getName().endsWith(".txt");
			}
		};
	}
	
	
	@Bean
	public MessageHandler targetDirectory() {
	    FileWritingMessageHandler handler = new FileWritingMessageHandler(new File("C:\\Users\\craig\\Documents\\"));
	    handler.setFileExistsMode(FileExistsMode.REPLACE);
	    handler.setExpectReply(false);
	    handler.setDeleteSourceFiles(true);
	    return handler;
	}
	
	@Bean
	public FileToStringTransformer fileToStringTransformer() {
		return new FileToStringTransformer();
	}

	

}

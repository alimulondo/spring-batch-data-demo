package ali.mulondo.dateextractiondemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.streaming.StreamingXlsxItemReader;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;

@Configuration
public class BatchConfig {
    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    Logger logger = LoggerFactory.getLogger(BatchConfig.class);

    @Bean
    public Job RunTestJob(Step step){
        return jobBuilderFactory.get("RunTestJob")
                .incrementer(new RunIdIncrementer())
                .flow(step)
                .end()
                .build();

    }

    @Bean
    public Step step(){
        return stepBuilderFactory.get("stpe1")
                .<String[],String[]>chunk(5)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean
    public StreamingXlsxItemReader<String[]> reader(){

        var reader = new StreamingXlsxItemReader<String[]>();
        reader.setName("TestReader");
        reader.setResource(new ClassPathResource("files/test_data.xlsx"));
        //note: month format display in Excel is 14/06/2021 but extracted value is (6/14/21)
        reader.setRowMapper(RowSet::getCurrentRow);

        return reader;
    }


    @Bean
    public ItemWriter<String[]> writer(){
        return items -> {
            for(var item : items){
                logger.info(Arrays.deepToString(item));
                // item 5 is supposed to be (02/02/1920) but extract is 2/2/20!
            }
        };

    }


}

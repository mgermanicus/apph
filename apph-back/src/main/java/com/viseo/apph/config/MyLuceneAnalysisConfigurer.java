package com.viseo.apph.config;

import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyLuceneAnalysisConfigurer implements LuceneAnalysisConfigurer {
    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {
        context.analyzer( "english" ).custom()
                .tokenizer( "standard" )
                .tokenFilter( "lowercase" )
                .tokenFilter( "snowballPorter" )
                .param( "language", "English" )
                .tokenFilter( "asciiFolding" );

        context.analyzer( "name" ).custom()
                .tokenizer( "standard" )
                .tokenFilter( "lowercase" )
                .tokenFilter( "asciiFolding" );
    }
}
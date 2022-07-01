package com.viseo.apph.config;

import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.model.dsl.LuceneAnalysisComponentParametersStep;
import org.hibernate.search.backend.lucene.analysis.model.dsl.LuceneAnalyzerOptionalComponentsStep;
import org.hibernate.search.backend.lucene.analysis.model.dsl.LuceneAnalyzerTokenizerStep;
import org.hibernate.search.backend.lucene.analysis.model.dsl.LuceneAnalyzerTypeStep;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MyLuceneAnalysisConfigurerTest {

    MyLuceneAnalysisConfigurer myLuceneAnalysisConfigurer = new MyLuceneAnalysisConfigurer();

    @Mock
    LuceneAnalysisConfigurationContext context;

    @Test
    public void testMyLuceneAnalysisConfigurer() {
        //GIVEN
        LuceneAnalyzerTypeStep luceneAnalyzerTypeStep = mock(LuceneAnalyzerTypeStep.class);
        LuceneAnalyzerTokenizerStep luceneAnalyzerTokenizerStep = mock(LuceneAnalyzerTokenizerStep.class);
        LuceneAnalyzerOptionalComponentsStep luceneAnalyzerOptionalComponentsStep = mock(LuceneAnalyzerOptionalComponentsStep.class);
        LuceneAnalysisComponentParametersStep luceneAnalysisComponentParametersStep = mock(LuceneAnalysisComponentParametersStep.class);
        when(context.analyzer(anyString())).thenReturn(luceneAnalyzerTypeStep);
        when(luceneAnalyzerTypeStep.custom()).thenReturn(luceneAnalyzerTokenizerStep);
        when(luceneAnalyzerTokenizerStep.tokenizer(anyString())).thenReturn(luceneAnalyzerOptionalComponentsStep);
        when(luceneAnalyzerOptionalComponentsStep.tokenFilter(anyString())).thenReturn(luceneAnalysisComponentParametersStep);
        //WhEN
        myLuceneAnalysisConfigurer.configure(context);
        //THEN
        verify(context,times(1)).analyzer(anyString());
        verify(luceneAnalysisComponentParametersStep,times(1)).tokenFilter(anyString());
    }
}

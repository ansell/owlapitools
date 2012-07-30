package org.coode.suggestor.api;

/**
 * Extracted interface from SuggestorFactoryImpl so that it can be used for service discovery.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public interface SuggestorFactory
{
    
    PropertySuggestor getPropertySuggestor();
    
    FillerSuggestor getFillerSuggestor();
    
}
package org.hl7.fhir.dstu3.hapi.ctx;

import static org.junit.Assert.*;

import org.hl7.fhir.dstu3.context.IWorkerContext.ValidationResult;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.dstu3.model.ValueSet.ConceptSetComponent;
import org.hl7.fhir.dstu3.model.ValueSet.ValueSetComposeComponent;
import org.hl7.fhir.dstu3.model.CodeSystem.CodeSystemContentMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ca.uhn.fhir.context.FhirContext;

@RunWith(MockitoJUnitRunner.class)
public class HapiWorkerContextTest {
    @Mock 
    private FhirContext ctx;
    @Mock
    private IValidationSupport validationSupport;
    @InjectMocks
    private HapiWorkerContext hapiWorkerContext;
    
    /**
     * This is the current version. Our implementation returns all code systems it finds, not presents included
     */
    @Test
    public void testValidatingNotPresentCodeSystem() {
        String system = "urn:oid:1.2.246.537.6.55";
        CodeSystem codeSystem = new CodeSystem();
        codeSystem.setContent(CodeSystemContentMode.NOTPRESENT);
        Mockito.when(validationSupport.fetchCodeSystem(ctx, system)).thenReturn(codeSystem);
        ValueSetComposeComponent composeComponent = new ValueSetComposeComponent();
        ConceptSetComponent conceptSetComponent = new ConceptSetComponent();
        conceptSetComponent.setSystem(system);
        composeComponent.addInclude(conceptSetComponent);
        ValueSet valueSet = new ValueSet();
        valueSet.setCompose(composeComponent);
   
        ValidationResult result = hapiWorkerContext.validateCode(system, "168120", "Amoxicillin (substance)",  valueSet);
       
        assertTrue(String.format("Expecting ok result, returned non-ok result with message: %s", result.getMessage()),
                result.isOk());
    }
    
    /**
     * This version working with our old validation support. It returned null when codesystem was not-present
     */
    @Test
    public void testValidatingNotFoundCodesystem() {
        String system = "urn:oid:1.2.246.537.6.55";
        Mockito.when(validationSupport.fetchCodeSystem(ctx, system)).thenReturn(null);
        ValueSetComposeComponent composeComponent = new ValueSetComposeComponent();
        ConceptSetComponent conceptSetComponent = new ConceptSetComponent();
        conceptSetComponent.setSystem(system);
        composeComponent.addInclude(conceptSetComponent);
        ValueSet valueSet = new ValueSet();
        valueSet.setCompose(composeComponent);
       
        ValidationResult result = hapiWorkerContext.validateCode(system, "168120", "Amoxicillin (substance)",  valueSet);
        
        assertFalse(String.format("Expecting ok result, returned non-ok result with message: %s", result.getMessage()),
                result.isOk());
        
    }

}

package com.kyndi.documents;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@ContextConfiguration(
    loader = SpringBootContextLoader.class,
    classes = Application.class
)
@WebAppConfiguration
public class DocumentResourceTest {
  private static final String largePdfFileName = "AAR0704.pdf";

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  public void testDocumentListEndpoint() throws Exception {
    this.mockMvc.perform(get("/documents").accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$.results").isArray())
        .andExpect(jsonPath("$.count").value(133));
  }

  @Test
  public void testDocumentMetadataEndpoint() throws Exception {
    this.mockMvc.perform(get("/documents/AAB0102.pdf/metadata"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$.name").value("AAB0102.pdf"))
        .andExpect(jsonPath("$.md5Checksum").isString())
        .andExpect(jsonPath("$.size").isNumber())
        .andExpect(jsonPath("$.lastModified").isNotEmpty());
  }

  @Test
  public void testDocumentDownloadEndpoint() throws Exception {
    this.mockMvc.perform(get("/documents/AAB0202.pdf").accept(MediaType.APPLICATION_PDF_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE));
  }

}

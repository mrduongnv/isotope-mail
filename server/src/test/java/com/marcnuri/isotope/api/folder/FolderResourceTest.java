/*
 * FolderResourceTest.java
 *
 * Created on 2018-09-23, 19:35
 *
 * Copyright 2018 Marc Nuri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.marcnuri.isotope.api.folder;

import com.marcnuri.isotope.api.credentials.Credentials;
import com.marcnuri.isotope.api.credentials.CredentialsService;
import com.marcnuri.isotope.api.imap.ImapService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Marc Nuri <marc@marcnuri.com> on 2018-09-23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = FolderResource.class)
public class FolderResourceTest {

    @Autowired
    private FolderResource folderResource;

    @MockBean
    private CredentialsService credentialsService;
    @MockBean
    private ImapService imapService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(folderResource).build();
        doReturn(new Credentials()).when(credentialsService).fromRequest(Mockito.any());
    }

    @After
    public void tearDown() {
        mockMvc = null;
    }

    @Test
    public void getFolders_na_shouldReturnOk() throws Exception {
        // Given
        final String folderId = "1337";
        final Folder mockFolder = new Folder();
        mockFolder.setChildren(new Folder[0]);
        mockFolder.setFolderId(folderId);
        doReturn(Collections.singletonList(mockFolder))
                .when(imapService).getFolders(Mockito.any(), Mockito.isNull());

        // When
        final ResultActions result = mockMvc.perform(
                get("/v1/folders")
                        .accept(MediaTypes.HAL_JSON_VALUE));

        // Then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").isArray());
        result.andExpect(jsonPath("[0].folderId").value(folderId));
        result.andExpect(jsonPath("[0]._links").exists());
        result.andExpect(jsonPath("[0]._links.messages.href", endsWith("/v1/folders/1337/messages")));
    }

}
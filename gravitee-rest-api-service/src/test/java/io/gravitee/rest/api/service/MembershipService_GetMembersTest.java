/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.rest.api.service;

import io.gravitee.repository.management.api.MembershipRepository;
import io.gravitee.repository.management.model.Membership;
import io.gravitee.repository.management.model.MembershipMemberType;
import io.gravitee.repository.management.model.MembershipReferenceType;
import io.gravitee.rest.api.model.MemberEntity;
import io.gravitee.rest.api.model.RoleEntity;
import io.gravitee.rest.api.model.UserEntity;
import io.gravitee.rest.api.model.permissions.SystemRole;
import io.gravitee.rest.api.service.MembershipService;
import io.gravitee.rest.api.service.RoleService;
import io.gravitee.rest.api.service.UserService;
import io.gravitee.rest.api.service.impl.MembershipServiceImpl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

/**
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class MembershipService_GetMembersTest {

    private static final String API_ID = "api-id-1";

    @InjectMocks
    private MembershipService membershipService = new MembershipServiceImpl();

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Test
    public void shouldGetEmptyMembersWithMembership() throws Exception {
        when(membershipRepository.findByReferencesAndRoleId(MembershipReferenceType.API, Collections.singletonList(API_ID), "PRIMARY_OWNER"))
                .thenReturn(Collections.emptySet());

        Set<MemberEntity> members = membershipService.getMembersByReferenceAndRole(io.gravitee.rest.api.model.MembershipReferenceType.API, API_ID, "PRIMARY_OWNER");

        Assert.assertNotNull(members);
        Assert.assertTrue("members must be empty", members.isEmpty());
        verify(membershipRepository, times(1)).findByReferencesAndRoleId(MembershipReferenceType.API, Collections.singletonList(API_ID), "PRIMARY_OWNER");
    }

    @Test
    public void shouldGetMembersWithMembership() throws Exception {
        Membership membership = new Membership();
        membership.setReferenceId(API_ID);
        membership.setCreatedAt(new Date());
        membership.setUpdatedAt(membership.getCreatedAt());
        membership.setReferenceType(MembershipReferenceType.API);
        membership.setRoleId("API_PRIMARY_OWNER");
        membership.setMemberId("user-id");
        membership.setMemberType(MembershipMemberType.USER);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(membership.getMemberId());
        userEntity.setFirstname("John");
        userEntity.setLastname("Doe");
        RoleEntity po = mock(RoleEntity.class);
        po.setName(SystemRole.PRIMARY_OWNER.name());
        when(membershipRepository.findByReferencesAndRoleId(MembershipReferenceType.API, Collections.singletonList(API_ID), "API_PRIMARY_OWNER"))
                .thenReturn(Collections.singleton(membership));
        when(userService.findById(membership.getMemberId())).thenReturn(userEntity);
        when(roleService.findById("API_PRIMARY_OWNER")).thenReturn(po);
        Set<MemberEntity> members = membershipService.getMembersByReferenceAndRole(io.gravitee.rest.api.model.MembershipReferenceType.API, API_ID, "API_PRIMARY_OWNER");

        Assert.assertNotNull(members);
        Assert.assertFalse("members must not be empty", members.isEmpty());
        verify(membershipRepository, times(1)).findByReferencesAndRoleId(MembershipReferenceType.API, Collections.singletonList(API_ID), "API_PRIMARY_OWNER");
        verify(userService, times(1)).findById(membership.getMemberId());
    }

    @Test
    public void shouldGetMembersWithoutMembership() throws Exception {
        Membership membership = new Membership();
        membership.setReferenceId(API_ID);
        membership.setCreatedAt(new Date());
        membership.setUpdatedAt(membership.getCreatedAt());
        membership.setReferenceType(MembershipReferenceType.API);
        membership.setRoleId("API_PRIMARY_OWNER");
        membership.setMemberId("user-id");
        membership.setMemberType(MembershipMemberType.USER);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(membership.getMemberId());
        userEntity.setFirstname("John");
        userEntity.setLastname("Doe");
        RoleEntity po = mock(RoleEntity.class);
        po.setName(SystemRole.PRIMARY_OWNER.name());
        when(roleService.findById("API_PRIMARY_OWNER")).thenReturn(po);
        when(membershipRepository.findByReferencesAndRoleId(MembershipReferenceType.API, Collections.singletonList(API_ID), null))
                .thenReturn(Collections.singleton(membership));
        when(userService.findById(membership.getMemberId())).thenReturn(userEntity);

        Set<MemberEntity> members = membershipService.getMembersByReferenceAndRole(io.gravitee.rest.api.model.MembershipReferenceType.API, API_ID, null);

        Assert.assertNotNull(members);
        Assert.assertFalse("members must not be empty", members.isEmpty());
        verify(membershipRepository, times(1)).findByReferencesAndRoleId(MembershipReferenceType.API, Collections.singletonList(API_ID), null);
        verify(userService, times(1)).findById(membership.getMemberId());
    }

}
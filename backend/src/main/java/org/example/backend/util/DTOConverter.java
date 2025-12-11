package org.example.backend.util;

import org.example.backend.dto.UserDTO;
import org.example.backend.dto.OrganizationDTO;
import org.example.backend.entity.User;
import org.example.backend.entity.Organization;

import java.util.List;
import java.util.stream.Collectors;

public class DTOConverter {

    // User -> UserDTO
    public static UserDTO toUserDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        // ⭐ 关键：把 enabled 映射进去
        dto.setEnabled(user.getEnabled());

        // 建议保持 LocalDateTime，而不是 toString()
        dto.setCreateTime(String.valueOf(user.getCreatedAt()));

        return dto;
    }

    public static List<UserDTO> toUserDTOList(List<User> users) {
        return users.stream()
                .map(DTOConverter::toUserDTO)
                .collect(Collectors.toList());
    }

    // Organization -> OrganizationDTO
    public static OrganizationDTO toOrganizationDTO(Organization org) {
        if (org == null) return null;
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(org.getId());
        dto.setName(org.getName());
        dto.setCertificateNo(org.getCertificateNo());
        dto.setContactName(org.getContactName());
        dto.setEmail(org.getEmail());
        dto.setPhone(org.getPhone());
        dto.setAddress(org.getAddress());
        dto.setDescription(org.getDescription());
        dto.setStatus(org.getStatus());

        // ⭐ 关键：把 enabled 映射进去
        dto.setEnabled(org.getEnabled());

        dto.setCreateTime(String.valueOf(org.getCreatedAt()));

        return dto;
    }

    public static List<OrganizationDTO> toOrganizationDTOList(List<Organization> orgs) {
        return orgs.stream()
                .map(DTOConverter::toOrganizationDTO)
                .collect(Collectors.toList());
    }
}

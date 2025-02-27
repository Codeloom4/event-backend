package com.codeloon.ems.service;

import com.codeloon.ems.dto.EventDto;
import com.codeloon.ems.dto.PackageDto;
import com.codeloon.ems.dto.PackageItemDto;
import com.codeloon.ems.entity.*;
import com.codeloon.ems.entity.Package;
import com.codeloon.ems.model.PackageMgtAccessBean;
import com.codeloon.ems.model.PackageTypeBean;
import com.codeloon.ems.repository.*;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final PackageItemRepository packageItemRepository;

    private final PackageTypeRepository packageTypeRepository;

    @Override
    public ResponseBean access() {
        ResponseBean responseBean = new ResponseBean();
        String msg = null;
        String code = ResponseCode.RSP_ERROR;
        try {
            // Fetch Event and User entities
            List<EventDto> events = eventRepository.findAll()
                    .stream()
                    .map(event -> new EventDto(event.getEventType(), event.getDescription()))
                    .collect(Collectors.toList());

            List<PackageTypeBean> packageTypes = packageTypeRepository.findAll()
                    .stream()
                    .map(pt -> new PackageTypeBean(pt.getCode(), pt.getDescription()))
                    .collect(Collectors.toList());

            // Populate PackageMgtAccessBean
            responseBean.setContent(new PackageMgtAccessBean(events, packageTypes));
            code = ResponseCode.RSP_SUCCESS;
        } catch (Exception ex) {
            log.error("Error occurred while creating package: {}", ex.getMessage(), ex);
            msg = "Error occurred while creating package.";
        }

        responseBean.setResponseMsg(msg);
        responseBean.setResponseCode(code);
        return responseBean;
    }

    @Override
    @Transactional
    public ResponseBean createPackage(PackageDto pack) {
        ResponseBean responseBean = new ResponseBean();
        String msg = null;
        String code = ResponseCode.RSP_ERROR;
        try {
            // Fetch Event and User entities
            Event event = eventRepository.findById(pack.getEventType())
                    .orElseThrow(() -> new RuntimeException("Event not found"));
            User createdUser = userRepository.findByUsername(pack.getCreatedUser())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            PackageType packageType = packageTypeRepository.findById(pack.getType())
                    .orElseThrow(()->new RuntimeException("Package type not found"));

            // Convert DTO to Entity
            Package newPackage = Package.builder()
                    .id(pack.getId())
                    .name(pack.getName())
                    .package_type(packageType)
                    .event(event)
                    .description(pack.getDescription())
                    .createdUser(createdUser)
                    .build();

            // Save the new package
            packageRepository.save(newPackage);
            code = ResponseCode.RSP_SUCCESS;
        } catch (Exception ex) {
            log.error("Error occurred while creating package: {}", ex.getMessage(), ex);
            msg = "Error occurred while creating package.";
        }

        responseBean.setResponseMsg(msg);
        responseBean.setResponseCode(code);
        return responseBean;
    }

    @Override
    @Transactional
    public ResponseBean updatePackage(PackageDto pack) {
        ResponseBean responseBean = new ResponseBean();
        String msg = null;
        String code = ResponseCode.RSP_ERROR;
        try {
            // Fetch the existing package
            Package existingPackage = packageRepository.findById(pack.getId())
                    .orElseThrow(() -> new RuntimeException("Package not found"));

            // Fetch Event and User entities
            Event event = eventRepository.findById(pack.getEventType())
                    .orElseThrow(() -> new RuntimeException("Event not found"));
            User createdUser = userRepository.findById(pack.getCreatedUser())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            PackageType packageType = packageTypeRepository.findById(pack.getType())
                    .orElseThrow(()->new RuntimeException("Package type not found"));

            // Update the package details
            existingPackage.setName(pack.getName());
            existingPackage.setPackage_type(packageType);
            existingPackage.setEvent(event);
            existingPackage.setDescription(pack.getDescription());
            existingPackage.setCreatedUser(createdUser);
            existingPackage.setUpdatedAt(LocalDateTime.now());

            // Save the updated package
            packageRepository.save(existingPackage);
            code = ResponseCode.RSP_SUCCESS;

        } catch (Exception ex) {
            log.error("Error occurred while updating package: {}", ex.getMessage(), ex);
            msg = "Error occurred while updating package.";
        }

        responseBean.setResponseMsg(msg);
        responseBean.setResponseCode(code);
        return responseBean;
    }

    @Override
    @Transactional
    public ResponseBean deletePackage(String packageId) {
        ResponseBean responseBean = new ResponseBean();
        String msg = null;
        String code = ResponseCode.RSP_ERROR;
        try {
            // Check if the package exists
            if (packageRepository.existsById(packageId)) {
                // Delete the package
                packageRepository.deleteById(packageId);
                code = ResponseCode.RSP_SUCCESS;

            } else {
                log.error("Package not found : {}", packageId);
                msg = "Package not found.";
            }

        } catch (Exception ex) {
            log.error("Error occurred while deleting package: {}", ex.getMessage(), ex);
            msg = "Error occurred while deleting package.";
        }

        responseBean.setResponseMsg(msg);
        responseBean.setResponseCode(code);
        return responseBean;
    }

    @Override
    @Transactional
    public ResponseBean createPackageItem(PackageItemDto packageItemDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = null;
        String code = ResponseCode.RSP_ERROR;

        try {
            // Fetch the associated Package
            Package packageEntity = packageRepository.findById(packageItemDto.getPackage_id())
                    .orElseThrow(() -> new RuntimeException("Package not found"));

            // Fetch the created User
            User createdUser = userRepository.findByUsername(packageItemDto.getCreatedUser())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Convert DTO to Entity
            PackageItem packageItem = PackageItem.builder()
                    .package_id(packageEntity) // Associate with the Package
                    .itemName(packageItemDto.getItemName())
                    .sellPrice(packageItemDto.getSellPrice())
                    .createdUser(createdUser)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Save the PackageItem
            packageItemRepository.save(packageItem);

            // Update the Package's packageItems list (if bidirectional relationship is maintained)
            packageEntity.getPackageItems().add(packageItem);

            code = ResponseCode.RSP_SUCCESS;
            msg = "Package item created successfully";
        } catch (Exception ex) {
            log.error("Error occurred while creating package item: {}", ex.getMessage(), ex);
            msg = "Error occurred while creating package item";
        }

        responseBean.setResponseCode(code);
        responseBean.setResponseMsg(msg);
        return responseBean;
    }
}

package com.codeloon.ems.service.impl;

import com.codeloon.ems.dto.EventDto;
import com.codeloon.ems.dto.PackageDto;
import com.codeloon.ems.dto.PackageInfoDTO;
import com.codeloon.ems.dto.PackageItemDto;
import com.codeloon.ems.entity.Package;
import com.codeloon.ems.entity.*;
import com.codeloon.ems.model.PackageMgtAccessBean;
import com.codeloon.ems.model.PackageTypeBean;
import com.codeloon.ems.model.PackageViewBean;
import com.codeloon.ems.model.PaginatedResponse;
import com.codeloon.ems.repository.*;
import com.codeloon.ems.service.ImageUploadService;
import com.codeloon.ems.service.PackageService;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private final InventoryItemRepository inventoryItemRepository;

    private final ImageUploadService imageUploadService;

    private final PackageSlideRepository packageSlideRepository;

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
    @CacheEvict(value = "packages", allEntries = true)
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
                    .orElseThrow(() -> new RuntimeException("Package type not found"));

            // Convert DTO to Entity
            Package newPackage = Package.builder()
                    .id(pack.getId())
                    .name(pack.getName())
                    .package_type(packageType)
                    .event(event)
                    .packagePrice(0.0)
                    .description(pack.getDescription())
                    .createdUser(createdUser)
                    .build();

            // Save the new package
            packageRepository.save(newPackage);
            code = ResponseCode.RSP_SUCCESS;
            msg = "Successfully created package!";
        } catch (Exception ex) {
            log.error("Error occurred while creating package: {}", ex.getMessage(), ex);
            msg = "Error occurred while creating package.";
        }

        responseBean.setResponseMsg(msg);
        responseBean.setResponseCode(code);
        return responseBean;
    }

    @Override
    @CacheEvict(value = "packages", allEntries = true)
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
                    .orElseThrow(() -> new RuntimeException("Package type not found"));

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
    @CacheEvict(value = "packages", allEntries = true)
    @Transactional
    public ResponseBean deletePackage(String packageId) {
        ResponseBean responseBean = new ResponseBean();
        String msg = null;
        String code = ResponseCode.RSP_ERROR;
        try {
            // Check if the package exists
            if (packageRepository.existsById(packageId)) {
                // Delete the package item
                packageItemRepository.deleteByPackageId(packageId);
                // Delete the package images
                imageUploadService.deleteImages(packageId);
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
    @CacheEvict(value = "packages", allEntries = true)
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

            InventoryItem inventoryItem = inventoryItemRepository.findInventoryItemById(Long.valueOf(packageItemDto.getItemCode()))
                    .orElseThrow(() -> new RuntimeException("Inventory item not found"));


            if (ObjectUtils.isEmpty(inventoryItem)) {
                msg = "Inventory not found";
            } else if (inventoryItem.getQuantity() < packageItemDto.getQuantity()) {
                msg = "Inventory Quantity Exceeds";
            } else {
                // Convert DTO to Entity
                PackageItem packageItem = PackageItem.builder()
                        .package_id(packageEntity) // Associate with the Package
                        .itemName(inventoryItem.getItemName())
                        .itemCode(packageItemDto.getItemCode())
                        .sellPrice(Double.valueOf(inventoryItem.getAvgPrice()))
                        .itemCategory(packageItemDto.getItemCategory())
                        .bulkPrice(packageItemDto.getBulkPrice())
                        .quantity(packageItemDto.getQuantity())
                        .createdUser(createdUser)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                // Save the PackageItem
                packageItemRepository.save(packageItem);

                // Update package total price.
                updatePackagePrice(packageEntity, packageItemDto.getPackage_id());

                code = ResponseCode.RSP_SUCCESS;
                msg = "Item added successfully";
            }

        } catch (Exception ex) {
            log.error("Error occurred while creating package item: {}", ex.getMessage(), ex);
            msg = "Error occurred while creating package item";
        }

        responseBean.setResponseCode(code);
        responseBean.setResponseMsg(msg);
        return responseBean;
    }

    @Override
    public ResponseBean getPackagesByEventType(String event) {
        ResponseBean responseBean = new ResponseBean();
        String msg = null;
        String code = ResponseCode.RSP_ERROR;

        try {

            // Fetch Event and User entities
            Event eventType = eventRepository.findById(event)
                    .orElseThrow(() -> new RuntimeException("Event not found"));

            List<Package> existingPackages = packageRepository.findPackagesByEvent(eventType);

            if (!existingPackages.isEmpty()) {
                List<PackageInfoDTO> packageInfoDTOS = new ArrayList<>();
                for (Package p : existingPackages) {
                    PackageInfoDTO dto = getPackageInfoDTO(p);
                    packageInfoDTOS.add(dto);
                }
                responseBean.setContent(packageInfoDTOS);
                code = ResponseCode.RSP_SUCCESS;
                msg = "Packages retrieval success";
            } else {
                code = ResponseCode.RSP_SUCCESS;
                msg = "No Packages for selected event";
            }


        } catch (Exception ex) {
            log.error("Error occurred while retrieving packages: {}", ex.getMessage(), ex);
            msg = "Error occurred while retrieving packages";
        }

        responseBean.setResponseCode(code);
        responseBean.setResponseMsg(msg);
        return responseBean;
    }

    @Override
    @CacheEvict(value = "packages", allEntries = true)
    @Transactional
    public ResponseBean updatePackageItem(String packageId, PackageItemDto packageItemDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = null;
        String code = ResponseCode.RSP_ERROR;

        try {
            // Fetch the existing PackageItem by packageId and itemCode
            PackageItem packageItem = packageItemRepository.findByPackageIdAndItemCode(packageId, packageItemDto.getItemCode())
                    .orElseThrow(() -> new RuntimeException("Package item not found"));

            // Fetch the associated Package
            Package packageEntity = packageRepository.findById(packageId)
                    .orElseThrow(() -> new RuntimeException("Package not found"));

            // Fetch the updated User
            User updatedUser = userRepository.findByUsername(packageItemDto.getCreatedUser())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Fetch the Inventory Item
            InventoryItem inventoryItem = inventoryItemRepository.findInventoryItemById(Long.valueOf(packageItemDto.getItemCode()))
                    .orElseThrow(() -> new RuntimeException("Inventory item not found"));

            if (ObjectUtils.isEmpty(inventoryItem)) {
                msg = "Inventory not found";
            } else if (inventoryItem.getQuantity() < packageItemDto.getQuantity()) {
                msg = "Inventory Quantity Exceeds";
            } else {
                // Update the PackageItem entity
                packageItem.setPackage_id(packageEntity);
                packageItem.setItemName(packageItemDto.getItemCode());
                packageItem.setItemName(packageItemDto.getItemName());
                packageItem.setItemCategory(packageItemDto.getItemCategory());
                packageItem.setBulkPrice(packageItemDto.getBulkPrice());
                packageItem.setQuantity(packageItemDto.getQuantity());
                packageItem.setCreatedUser(updatedUser);
                packageItem.setUpdatedAt(LocalDateTime.now());

                // Save the updated PackageItem
                packageItemRepository.saveAndFlush(packageItem);

                // Update package total price.
                updatePackagePrice(packageEntity, packageItemDto.getPackage_id());

                msg = "Package item updated successfully";
                code = ResponseCode.RSP_SUCCESS;
            }
        } catch (Exception ex) {
            log.error("Error occurred while updating package item: {}", ex.getMessage(), ex);
            msg = "Error occurred while updating package item";
        }

        responseBean.setResponseCode(code);
        responseBean.setResponseMsg(msg);
        return responseBean;
    }

    @Override
    @CacheEvict(value = "packages", allEntries = true)
    @Transactional
    public ResponseBean deletePackageItem(String itemCode, String packageId) {
        ResponseBean responseBean = new ResponseBean();
        String msg = null;
        String code = ResponseCode.RSP_ERROR;
        try {
            // Fetch the existing PackageItem by packageId and itemCode
            PackageItem packageItem = packageItemRepository.findByPackageIdAndItemCode(packageId, itemCode).orElse(null);

            if (packageItem == null) {
                msg = "Package item not found";
            } else {
                packageItemRepository.deleteByPackageIdAndItemCode(packageId, itemCode);
                msg = "Package item deleted successfully";
                code = ResponseCode.RSP_SUCCESS;
            }

        } catch (Exception ex) {
            log.error("Error occurred while deleting package item: {}", ex.getMessage(), ex);
            msg = "Error occurred while deleting package item";
        }

        responseBean.setResponseCode(code);
        responseBean.setResponseMsg(msg);
        responseBean.setContent(null);
        return responseBean;
    }

    @Override
    public ResponseBean getPackageItems(String packageId) {
        ResponseBean responseBean = new ResponseBean();
        String msg = null;
        String code = ResponseCode.RSP_ERROR;
        List<PackageItem> packageItems = new ArrayList<>();
        List<PackageItemDto> packageItemDtos = new ArrayList<>();
        try {
            packageItems = packageItemRepository.findByPackageId(packageId);

            if (packageItems.isEmpty()) {
                msg = "No package items found for the given Package ID";
            } else {

                for (PackageItem item : packageItems) {
                    PackageItemDto dto = PackageItemDto.builder()
                            .itemCode(item.getItemCode())
                            .itemCode(item.getItemName())
                            .itemName(inventoryItemRepository.getItemName(item.getItemCode()))
                            .bulkPrice(item.getBulkPrice())
                            .quantity(item.getQuantity())
                            .itemCategory(item.getItemCategory())
                            .createdUser(item.getCreatedUser().getUsername())
                            .updatedAt(item.getUpdatedAt())
                            .package_id(item.getPackage_id().getId())
                            .build();
                    packageItemDtos.add(dto);
                }

                msg = "Package items retrieved successfully";
                code = ResponseCode.RSP_SUCCESS;
            }
        } catch (Exception ex) {
            log.error("Error occurred while retrieving package items: {}", ex.getMessage(), ex);
            msg = "Error occurred while retrieving package items";
        }

        responseBean.setResponseCode(code);
        responseBean.setResponseMsg(msg);
        responseBean.setContent(packageItemDtos);
        return responseBean;
    }
    
    @Cacheable(value = "packages", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public ResponseBean getAllPackages(Pageable pageable) {
        ResponseBean responseBean = new ResponseBean();
        String msg;
        String code = ResponseCode.RSP_ERROR;

        try {
            List<PackageViewBean> packageViewBeanList = new ArrayList<>();

            // Fetch paginated packages
            Page<Package> packagePage = packageRepository.findAllByIsComplete(pageable, true);
            List<Package> existingPackages = packagePage.getContent();

            if (!existingPackages.isEmpty()) {
                for (Package p : existingPackages) {
                    PackageViewBean viewBean = new PackageViewBean();

                    // Set package info
                    viewBean.setPackageInfo(getPackageInfoDTO(p));

                    // Set package slides (images)
                    List<PackageSlide> images = packageSlideRepository.findByPackageId(p.getId());
                    List<PackageSlide> updatedImages = images.stream()
                            .map(image -> {
                                image.setFilePath("http://localhost:9999/packages/" + image.getFilePath());
                                return image;
                            })
                            .collect(Collectors.toList());
                    viewBean.setPackageSlides(updatedImages);
//                    viewBean.setPackageSlides(images);

                    // Set package items metntn ganin
                    List<PackageItem> packageItems = packageItemRepository.findByPackageId(p.getId());
                    List<PackageItemDto> packageItemDtos = new ArrayList<>();

                    for (PackageItem item : packageItems) {
                        PackageItemDto pItem = PackageItemDto.builder()
                                .itemCode(item.getItemCode())
                                .itemName(item.getItemName())
                                .bulkPrice(item.getBulkPrice())
                                .quantity(item.getQuantity())
                                .itemCategory(item.getItemCategory())
                                .createdUser(item.getCreatedUser().getUsername())
                                .updatedAt(item.getUpdatedAt())
                                .package_id(item.getPackage_id().getId())
                                .build();
                        packageItemDtos.add(pItem);
                    }
                    viewBean.setPackageItems(packageItemDtos);

                    packageViewBeanList.add(viewBean);
                }

                // Set paginated content and metadata
                PaginatedResponse<PackageViewBean> paginatedResponse = new PaginatedResponse<>();
                paginatedResponse.setContent(packageViewBeanList);
                paginatedResponse.setTotalPages(packagePage.getTotalPages());
                paginatedResponse.setTotalElements(packagePage.getTotalElements());
                paginatedResponse.setPageNumber(packagePage.getNumber());
                paginatedResponse.setPageSize(packagePage.getSize());

                responseBean.setContent(paginatedResponse);
                code = ResponseCode.RSP_SUCCESS;
                msg = "Packages retrieval success";
            } else {
                code = ResponseCode.RSP_SUCCESS;
                msg = "No packages found";
            }
        } catch (Exception ex) {
            log.error("Error occurred while retrieving packages: {}", ex.getMessage(), ex);
            msg = "Error occurred while retrieving packages";
        }

        responseBean.setResponseCode(code);
        responseBean.setResponseMsg(msg);
        return responseBean;
    }

    private static PackageInfoDTO getPackageInfoDTO(Package p) {
        PackageInfoDTO dto = new PackageInfoDTO();
        dto.setEventType(p.getEvent().getEventType());
        dto.setEventDescription(p.getEvent().getDescription());
        dto.setPackageType(p.getPackage_type().getCode());
        dto.setPackageTypeDescription(p.getPackage_type().getDescription());
        dto.setDescription(p.getDescription());
        dto.setName(p.getName());
        dto.setId(p.getId());
        dto.setPackagePrice(p.getPackagePrice());
        dto.setCreatedUser(p.getCreatedUser().getUsername());
        return dto;
    }

    private void updatePackagePrice(Package packageEntity, String packageId) {
        List<PackageItem> items = packageItemRepository.findByPackageId(packageId);
        Double packagePrice = items.stream()
                .mapToDouble(PackageItem::getBulkPrice)
                .sum();

        packageEntity.setPackagePrice(packagePrice);
        packageRepository.saveAndFlush(packageEntity);
    }

}

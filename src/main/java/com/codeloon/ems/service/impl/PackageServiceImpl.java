package com.codeloon.ems.service.impl;

import com.codeloon.ems.dto.*;
import com.codeloon.ems.entity.Package;
import com.codeloon.ems.entity.*;
import com.codeloon.ems.model.InventoryItemBean;
import com.codeloon.ems.model.PackageMgtAccessBean;
import com.codeloon.ems.model.PackageTypeBean;
import com.codeloon.ems.model.PackageViewBean;
import com.codeloon.ems.repository.*;
import com.codeloon.ems.service.ImageUploadService;
import com.codeloon.ems.service.PackageService;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            // Fetch Events
            List<EventDto> events = eventRepository.findAll()
                    .stream()
                    .map(event -> new EventDto(event.getEventType(), event.getDescription()))
                    .collect(Collectors.toList());

            // Fetch Package Types
            List<PackageTypeBean> packageTypes = packageTypeRepository.findAll()
                    .stream()
                    .map(pt -> new PackageTypeBean(pt.getCode(), pt.getDescription()))
                    .collect(Collectors.toList());

            // Fetch Unique Categories from Inventory Items
            List<String> uniqueCategories = inventoryItemRepository.findAll()
                    .stream()
                    .map(InventoryItem::getCategory) // Extract category
                    .distinct() // Remove duplicates
                    .collect(Collectors.toList());

            // Populate Response Bean
            responseBean.setContent(new PackageMgtAccessBean(events, packageTypes, uniqueCategories));
            code = ResponseCode.RSP_SUCCESS;
        } catch (Exception ex) {
            log.error("Error occurred while accessing package data: {}", ex.getMessage(), ex);
            msg = "Error occurred while accessing package data.";
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
                        .itemName(packageItemDto.getItemName())
                        .itemCode(packageItemDto.getItemCode())
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
            // Retrieve package items by packageId from the package_item table
            packageItems = packageItemRepository.findByPackageId(packageId);

            // Check if package items are found
            if (packageItems.isEmpty()) {
                msg = "No package items found for the given Package ID";
            } else {
                // Map package items to DTOs
                for (PackageItem item : packageItems) {
                    PackageItemDto dto = PackageItemDto.builder()
                            .itemCode(item.getItemCode())  // Corrected here
                            .itemName(item.getItemName())  // Assuming you want to include itemName
                            .bulkPrice(item.getBulkPrice())
                            .quantity(item.getQuantity())
                            .itemCategory(item.getItemCategory())
                            .createdUser(item.getCreatedUser().getUsername())
                            .updatedAt(item.getUpdatedAt())
                            .package_id(item.getPackage_id().getId()) // Ensure this is correctly fetched
                            .build();
                    packageItemDtos.add(dto);
                }

                // Successful message and response code
                msg = "Package items retrieved successfully";
                code = ResponseCode.RSP_SUCCESS;
            }
        } catch (Exception ex) {
            // Handle any exception that occurs during retrieval
            log.error("Error occurred while retrieving package items: {}", ex.getMessage(), ex);
            msg = "Error occurred while retrieving package items";
        }

        // Set the response code, message, and content
        responseBean.setResponseCode(code);
        responseBean.setResponseMsg(msg);
        responseBean.setContent(packageItemDtos);

        return responseBean;
    }


    public ResponseBean getAllPackages() {
        ResponseBean responseBean = new ResponseBean();
        String msg;
        String code = ResponseCode.RSP_ERROR;

        try {
            List<PackageViewBean> packageViewBeanList = new ArrayList<>();
            List<Package> existingPackages = packageRepository.findAll();

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
                                .itemName(item.getItemName()) // Fixed: Changed from .itemCode(item.getItemName())
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

                responseBean.setContent(packageViewBeanList);
                code = ResponseCode.RSP_SUCCESS;
                msg = "Packages retrieval success";
            } else {
                code = ResponseCode.RSP_SUCCESS;
                msg = "No packages found for the selected event";
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
        // Map Package entity to DTO (PackageInfoDTO)
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

//    @Override
//    public ResponseBean allPackageDetails() {
//        ResponseBean responseBean = new ResponseBean();
//        String msg = null;
//        String code = ResponseCode.RSP_ERROR;
//
//        try {
//            // Fetch all packages
//            List<Package> packages = packageRepository.findAll();
//            log.info("Packages found: {}", packages.size());
//
//            if (packages.isEmpty()) {
//                log.warn("No packages found in the database.");
//                responseBean.setContent(List.of()); // Return an empty list instead of null
//                responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
//                return responseBean;
//            }
//
//            // For each package, fetch associated package items
//            List<PackageWithItemsDto> packageWithItemsList = packages.stream().map(pkg -> {
//                log.info("Fetching items for package ID: {}", pkg.getId());
//
//                List<PackageItem> packageItems = packageItemRepository.findByPackageId(pkg.getId());
//                log.info("Package ID: {}, Items found: {}", pkg.getId(), packageItems.size());
//
//                // Map package items to DTOs
//                List<PackageItemDto> packageItemDtos = packageItems.stream().map(item ->
//                        new PackageItemDto(
//                                pkg.getId(),  // package_id
//                                item.getItemCode(),
//                                item.getBulkPrice(),
//                                item.getQuantity(),
//                                item.getItemName(),
//                                item.getItemCategory(),
//                                item.getCreatedAt(),
//                                item.getUpdatedAt(),
//                                item.getCreatedUser()
//                        )
//                ).collect(Collectors.toList());
//
//                // Map package to DTO
//                PackageDto packageDto = new PackageDto(
//                        pkg.getId(),
//                        pkg.getName(),
//                        pkg.getPackage_type(),
//                        pkg.getEvent(),
//                        pkg.getDescription(),
//                        pkg.getCreatedAt(),
//                        pkg.getUpdatedAt(),
//                        pkg.getCreatedUser()
//                );
//
//                // Return combined DTO
//                return new PackageWithItemsDto(packageDto, packageItemDtos);
//            }).collect(Collectors.toList());
//
//            // Set response data
//            responseBean.setContent(packageWithItemsList);
//            code = ResponseCode.RSP_SUCCESS;
//        } catch (Exception ex) {
//            log.error("Error occurred while accessing package data: {}", ex.getMessage(), ex);
//            msg = "Error occurred while accessing package data.";
//            responseBean.setContent(List.of()); // Ensure empty list is returned instead of null
//        }
//
//        responseBean.setResponseMsg(msg);
//        responseBean.setResponseCode(code);
//        return responseBean;
//    }

}

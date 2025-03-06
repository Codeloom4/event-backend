package com.codeloon.ems.service;

import com.codeloon.ems.entity.Grouping;
import com.codeloon.ems.model.Group;
import com.codeloon.ems.model.GroupingBean;
import com.codeloon.ems.model.Participant;
import com.codeloon.ems.repository.GroupingRepository;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupingServiceImpl implements GroupingService {

    private final GroupingRepository groupingRepository;
    private static final float BOTTOM_MARGIN = 50; // Bottom margin for the page (adjust as needed)

    @Value("${grouping.windows}")
    private String windowsGroupingPath;

    @Value("${grouping.linux}")
    private String linuxGroupingPath;

    @Override
    public ResponseBean createGrouping(GroupingBean groupingBean, MultipartFile file) {
        ResponseBean responseBean = new ResponseBean();
        try {
            Grouping grouping = new Grouping();
            grouping.setUsername(groupingBean.getUsername());
            grouping.setEventType(groupingBean.getEventType());
            grouping.setEventName(groupingBean.getEventName());
            grouping.setTotalParticipants(groupingBean.getTotalParticipants());
            grouping.setNumberOfGroups(groupingBean.getNumberOfGroups());
            grouping.setGroupingMethod(groupingBean.getGroupingMethod());

            if (file != null && !file.isEmpty()) {
                String filePath = saveFile(file);
                grouping.setFilePath(filePath);
            }

            grouping.setCreatedAt(LocalDateTime.now());
            grouping.setUpdatedAt(LocalDateTime.now());

            groupingRepository.save(grouping);
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Grouping created successfully.");
        } catch (Exception ex) {
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error creating grouping: " + ex.getMessage());
        }
        return responseBean;
    }

    @Override
    public ResponseBean updateGrouping(Long id, GroupingBean groupingBean, MultipartFile file) {
        ResponseBean responseBean = new ResponseBean();
        try {
            Optional<Grouping> optionalGrouping = groupingRepository.findById(id);
            if (optionalGrouping.isPresent()) {
                Grouping grouping = optionalGrouping.get();
                grouping.setEventType(groupingBean.getEventType());
                grouping.setEventName(groupingBean.getEventName());
                grouping.setTotalParticipants(groupingBean.getTotalParticipants());
                grouping.setNumberOfGroups(groupingBean.getNumberOfGroups());
                grouping.setGroupingMethod(groupingBean.getGroupingMethod());

                // Handle file update
                if (file != null && !file.isEmpty()) {
                    // Delete the old file if it exists
                    if (grouping.getFilePath() != null) {
                        Files.deleteIfExists(Paths.get(grouping.getFilePath()));
                    }
                    // Save the new file and update the filePath in the database
                    String filePath = saveFile(file);
                    grouping.setFilePath(filePath); // Update the filePath in the database
                }

                // Update updatedAt
                grouping.setUpdatedAt(LocalDateTime.now());

                groupingRepository.save(grouping);
                responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
                responseBean.setResponseMsg("Grouping updated successfully.");
            } else {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("Grouping not found.");
            }
        } catch (Exception ex) {
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error updating grouping: " + ex.getMessage());
        }
        return responseBean;
    }

    @Override
    public ResponseBean deleteGrouping(Long id) {
        ResponseBean responseBean = new ResponseBean();
        try {
            Optional<Grouping> optionalGrouping = groupingRepository.findById(id);
            if (optionalGrouping.isPresent()) {
                Grouping grouping = optionalGrouping.get();

                // Delete the associated file if it exists
                if (grouping.getFilePath() != null) {
                    Path filePath = Paths.get(grouping.getFilePath());
                    if (Files.exists(filePath)) {
                        Files.delete(filePath);
                    }
                }

                // Delete the record from the database
                groupingRepository.deleteById(id);

                responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
                responseBean.setResponseMsg("Grouping and associated file deleted successfully.");
            } else {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("Grouping not found.");
            }
        } catch (Exception ex) {
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error deleting grouping: " + ex.getMessage());
        }
        return responseBean;
    }


    @Override
    public List<GroupingBean> getGroupingsByUsername(String username) {
        return groupingRepository.findByUsername(username).stream()
                .map(this::convertToBean)
                .collect(Collectors.toList());
    }

    private String saveFile(MultipartFile file) throws IOException {
        String uploadDir = getGroupingUploadDir();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        return filePath.toString();
    }

    private String getGroupingUploadDir() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? windowsGroupingPath : linuxGroupingPath;
    }

    private GroupingBean convertToBean(Grouping grouping) {
        GroupingBean groupingBean = new GroupingBean();
        groupingBean.setId(grouping.getId());
        groupingBean.setUsername(grouping.getUsername());
        groupingBean.setEventType(grouping.getEventType());
        groupingBean.setEventName(grouping.getEventName());
        groupingBean.setTotalParticipants(grouping.getTotalParticipants());
        groupingBean.setNumberOfGroups(grouping.getNumberOfGroups());
        groupingBean.setGroupingMethod(grouping.getGroupingMethod());
        groupingBean.setFilePath(grouping.getFilePath());
        groupingBean.setCreatedAt(grouping.getCreatedAt());
        groupingBean.setUpdatedAt(grouping.getUpdatedAt());
        return groupingBean;
    }



    @Override
    public ResponseBean processGrouping(Long id) {
        ResponseBean responseBean = new ResponseBean();
        try {
            // Fetch the grouping record
            Optional<Grouping> optionalGrouping = groupingRepository.findById(id);
            if (optionalGrouping.isEmpty()) {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR); // Use your response code for errors
                responseBean.setResponseMsg("Grouping not found.");
                return responseBean;
            }

            Grouping grouping = optionalGrouping.get();

            // Validate the CSV file
            if (grouping.getFilePath() == null || !Files.exists(Paths.get(grouping.getFilePath()))) {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("CSV file not found.");
                return responseBean;
            }

            // Read the CSV file
            List<Participant> participants = readCsvFile(grouping.getFilePath());

            // Perform grouping based on the grouping method
            List<Group> groups = performGrouping(participants, grouping.getGroupingMethod(), grouping.getNumberOfGroups());

            // Generate the PDF
            byte[] pdfBytes = generatePdf(grouping, groups);

            // Set the response
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS); // Use your response code for success
            responseBean.setResponseMsg("Grouping processed successfully.");
            responseBean.setContent(pdfBytes); // Set the PDF bytes as the response content
        } catch (Exception ex) {
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error processing grouping: " + ex.getMessage());
        }
        return responseBean;
    }



    private List<Participant> readCsvFile(String filePath) throws IOException {
        List<Participant> participants = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length != 3) {
                    throw new IOException("Invalid CSV format: Expected 3 columns (Name, Age, Job Role).");
                }
                // Create a Participant object and add it to the list
                participants.add(new Participant(values[0].trim(), Integer.parseInt(values[1].trim()), values[2].trim()));
            }
        }
        return participants;
    }


    private List<Group> performGrouping(List<Participant> participants, String groupingMethod, Integer numberOfGroups) {
        List<Group> groups = new ArrayList<>();

        // If numberOfGroups is null, calculate it based on the number of participants
        if (numberOfGroups == null) {
            numberOfGroups = (int) Math.ceil((double) participants.size() / 5); // Default to 5 participants per group
        }

        // Initialize groups
        for (int i = 0; i < numberOfGroups; i++) {
            groups.add(new Group("Group " + (i + 1)));
        }

        // Perform grouping based on the grouping method
        switch (groupingMethod) {
            case "Random":
                Collections.shuffle(participants); // Shuffle participants randomly
                distributeParticipants(participants, groups);
                break;

            case "By Age Range":
                // Group participants by age range
                Map<String, List<Participant>> ageRangeMap = groupParticipantsByAgeRange(participants);
                distributeParticipantsByCategory(ageRangeMap, groups);
                break;

            case "By Family":
                // Group participants by family (surname)
                Map<String, List<Participant>> familyMap = groupParticipantsByFamily(participants);
                distributeParticipantsByCategory(familyMap, groups);
                break;

            case "By Job Role":
                // Group participants by job role
                Map<String, List<Participant>> jobRoleMap = groupParticipantsByJobRole(participants);
                distributeParticipantsByCategory(jobRoleMap, groups);
                break;

            default:
                throw new IllegalArgumentException("Invalid grouping method: " + groupingMethod);
        }

        return groups;
    }

    // Helper method to group participants by age range
    private Map<String, List<Participant>> groupParticipantsByAgeRange(List<Participant> participants) {
        Map<String, List<Participant>> ageRangeMap = new HashMap<>();
        ageRangeMap.put("0-20", new ArrayList<>());
        ageRangeMap.put("21-40", new ArrayList<>());
        ageRangeMap.put("41-60", new ArrayList<>());
        ageRangeMap.put("61+", new ArrayList<>());

        for (Participant participant : participants) {
            int age = participant.getAge();
            if (age <= 20) {
                ageRangeMap.get("0-20").add(participant);
            } else if (age <= 40) {
                ageRangeMap.get("21-40").add(participant);
            } else if (age <= 60) {
                ageRangeMap.get("41-60").add(participant);
            } else {
                ageRangeMap.get("61+").add(participant);
            }
        }
        return ageRangeMap;
    }
    // Helper method to group participants by family (surname)
    private Map<String, List<Participant>> groupParticipantsByFamily(List<Participant> participants) {
        Map<String, List<Participant>> familyMap = new HashMap<>();
        for (Participant participant : participants) {
            String surname = participant.getName().split(" ")[1]; // Extract surname
            familyMap.computeIfAbsent(surname, k -> new ArrayList<>()).add(participant);
        }
        return familyMap;
    }

    // Helper method to group participants by job role
    private Map<String, List<Participant>> groupParticipantsByJobRole(List<Participant> participants) {
        Map<String, List<Participant>> jobRoleMap = new HashMap<>();
        for (Participant participant : participants) {
            String jobRole = participant.getJobRole().toLowerCase();
            jobRoleMap.computeIfAbsent(jobRole, k -> new ArrayList<>()).add(participant);
        }
        return jobRoleMap;
    }
    private void distributeParticipantsByCategory(Map<String, List<Participant>> categoryMap, List<Group> groups) {
        // First, distribute participants from each category into groups
        for (List<Participant> categoryParticipants : categoryMap.values()) {
            distributeParticipants(categoryParticipants, groups);
        }

        // If there are any remaining participants (e.g., uneven distribution), mix them into the groups
        List<Participant> remainingParticipants = new ArrayList<>();
        for (List<Participant> categoryParticipants : categoryMap.values()) {
            remainingParticipants.addAll(categoryParticipants);
        }
        distributeParticipants(remainingParticipants, groups);
    }
    // Helper method to distribute participants into groups (round-robin)
    private void distributeParticipants(List<Participant> participants, List<Group> groups) {
        int groupIndex = 0;
        for (Participant participant : participants) {
            // Add the participant to the current group
            groups.get(groupIndex).addParticipant(participant);
            // Move to the next group (round-robin distribution)
            groupIndex = (groupIndex + 1) % groups.size();
        }
    }

    private byte[] generatePdf(Grouping grouping, List<Group> groups) throws IOException {
        // Create a new PDF document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Set font
        PDFont font = PDType1Font.HELVETICA_BOLD;
        int fontSize = 12;
        float margin = 50; // Padding from the edges
        float yPosition = 800 - margin; // Start at the top of the page

        // Add Cover Page
        yPosition = addCoverPage(contentStream, grouping, font, fontSize, margin, yPosition);
        contentStream.close();

        // Start new content stream for group details
        page = new PDPage(PDRectangle.A4); // Create a new page for group details
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page);
        yPosition = 800 - margin; // Reset yPosition for the new page

        // Add table header (only once on the first page after the cover page)
        yPosition = addTableHeader(contentStream, font, fontSize, margin, yPosition);

        // Iterate through groups and print them continuously
        for (Group group : groups) {
            // Check if there's enough space for the next group
            float requiredSpace = calculateRequiredSpaceForGroup(group, fontSize);
            if (yPosition - requiredSpace < BOTTOM_MARGIN) {
                // Not enough space, create a new page
                contentStream.close();
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                yPosition = 800 - margin; // Reset yPosition for the new page
                yPosition = addTableHeader(contentStream, font, fontSize, margin, yPosition); // Add header to the new page
            }

            // Add group details in table format
            yPosition = addGroupDetailsToTable(contentStream, group, font, fontSize, margin, yPosition);

            yPosition -= 20; // Add spacing between groups
        }

        contentStream.close();

        // Add page numbers
        addPageNumbers(document);

        // Convert document to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }
    private float calculateRequiredSpaceForGroup(Group group, int fontSize) {
        // Space for group name
        float spaceRequired = 20;

        // Space for each participant
        spaceRequired += group.getParticipants().size() * (fontSize + 10); // Adjust based on font size and spacing

        return spaceRequired;
    }

    // Helper method to add cover page design
    private float addCoverPage(PDPageContentStream contentStream, Grouping grouping, PDFont font, int fontSize, float margin, float yPosition) throws IOException {
        // Add a title
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Event Grouping Report");
        contentStream.endText(); // End the text block
        yPosition -= 40;

        // Add a horizontal line
        contentStream.setLineWidth(2); // Set line thickness
        contentStream.moveTo(margin, yPosition); // Start point of the line
        contentStream.lineTo(PDRectangle.A4.getWidth() - margin, yPosition); // End point of the line
        contentStream.stroke(); // Draw the line
        yPosition -= 40;

        // Add event details
        contentStream.beginText(); // Start a new text block
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Event Name: " + grouping.getEventName());
        yPosition -= 20;

        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Created By: " + grouping.getUsername());
        yPosition -= 20;

        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Created At: " + grouping.getCreatedAt().toString());
        yPosition -= 20;

        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Number of Groups: " + grouping.getNumberOfGroups());
        yPosition -= 20;

        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Grouping Method: " + grouping.getGroupingMethod());
        yPosition -= 20;

        contentStream.endText(); // End the text block
        return yPosition;
    }

    // Helper method to add table header
    private float addTableHeader(PDPageContentStream contentStream, PDFont font, int fontSize, float margin, float yPosition) throws IOException {
        // Draw table header
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Group");
        contentStream.newLineAtOffset(150, 0);
        contentStream.showText("Participants");
        contentStream.endText();

        yPosition -= 20;
        return yPosition;
    }

    // Helper method to add group details to the table
    private float addGroupDetailsToTable(PDPageContentStream contentStream, Group group, PDFont font, int fontSize, float margin, float yPosition) throws IOException {
        // Add group name
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText(group.getName());
        contentStream.endText();
        yPosition -= 20;

        // List participants in the group
        for (Participant participant : group.getParticipants()) {
            String participantDetails = participant.getName() + " (" + participant.getAge() + ", " + participant.getJobRole() + ")";
            contentStream.beginText();
            contentStream.setFont(font, fontSize - 2);
            contentStream.newLineAtOffset(margin + 150, yPosition);
            contentStream.showText(participantDetails);
            contentStream.endText();
            yPosition -= 20;
        }

        return yPosition;
    }

    // Helper method to add page numbers
    private void addPageNumbers(PDDocument document) throws IOException {
        int pageCount = document.getNumberOfPages();
        for (int i = 0; i < pageCount; i++) {
            PDPage page = document.getPage(i);
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);

            // Set font and font size for page number
            PDFont font = PDType1Font.HELVETICA_BOLD;
            int fontSize = 10;

            // Add page number at the bottom center of the page
            String pageNumber = "Page " + (i + 1) + " of " + pageCount;
            float textWidth = font.getStringWidth(pageNumber) / 1000 * fontSize;
            float xPosition = (PDRectangle.A4.getWidth() - textWidth) / 2; // Center the page number
            float yPosition = BOTTOM_MARGIN - 10; // Position above the bottom margin

            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(xPosition, yPosition);
            contentStream.showText(pageNumber);
            contentStream.endText();

            contentStream.close();
        }
    }

}



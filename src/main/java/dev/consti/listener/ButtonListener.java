package dev.consti.listener;

import dev.consti.ticket.TicketHandler;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;


import java.util.Objects;

public class ButtonListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (Objects.equals(event.getButton().getId(), "create_ticket")) {
            // Create buttons for Bug Report, Feature Request, and Custom
            Button bugReportButton = Button.primary("create_bug_report", "🐞 Bug Report");
            Button featureRequestButton = Button.primary("create_feature_request", "✨ Feature Request");
            Button customButton = Button.primary("create_custom_ticket", "📝 Custom");

            event.reply("Please choose the type of ticket:")
                    .addActionRow(bugReportButton, featureRequestButton, customButton)
                    .setEphemeral(true).queue();
        } else if (Objects.equals(event.getButton().getId(), "close_ticket")) {
            // Close the ticket
            event.getChannel().delete().queue();
        } else if (Objects.equals(event.getButton().getId(), "create_bug_report")) {
            // Send checklist message for Bug Report

            Button confirmChecklistButton = Button.primary("confirm_checklist", "✅ Confirm Checklist");

            event.editMessage("**Troubleshooting Checklist:**\n" +
                            "- [:question:] Installed the newest version of the plugin\n" +
                            "- [:question:] Checked compatibility with the current Minecraft version\n" +
                            "- [:question:] Verified Java version is compatible\n" +
                            "- [:question:] The scripts are correctly configured\n\n" +
                            "Please confirm that you have completed all the points above.")
                    .setActionRow(confirmChecklistButton)
                    .queue();
        } else if (Objects.equals(event.getButton().getId(), "confirm_checklist")) {
            event.getMessage().delete().queue();
            // Create and send a modal for Bug Report after confirming the checklist
            TextInput titleInput = TextInput.create("title", "Title", TextInputStyle.SHORT)
                    .setPlaceholder("Enter the bug title here...")
                    .setRequired(true)
                    .build();

            TextInput descriptionInput = TextInput.create("description", "Description", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Describe the bug here...")
                    .setRequired(true)
                    .build();

            TextInput stepsInput = TextInput.create("steps", "Steps to Reproduce", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("List the steps to reproduce the bug here...")
                    .setRequired(false)
                    .build();

            TextInput expectedInput = TextInput.create("expected", "Expected Behavior", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Describe what you expected to happen here...")
                    .setRequired(false)
                    .build();

            Modal bugReportModal = Modal.create("bug_report_modal", "Bug Report")
                    .addActionRow(titleInput)
                    .addActionRow(descriptionInput)
                    .addActionRow(stepsInput)
                    .addActionRow(expectedInput)
                    .build();
            event.replyModal(bugReportModal).queue();
        } else if (Objects.equals(event.getButton().getId(), "create_feature_request")) {
            // Delete the original message and create a modal for Feature Request
            event.getMessage().delete().queue();

            TextInput titleInput = TextInput.create("title", "Title", TextInputStyle.SHORT)
                    .setPlaceholder("Enter the feature title here...")
                    .setRequired(true)
                    .build();

            TextInput descriptionInput = TextInput.create("description", "Description", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Describe the feature here...")
                    .setRequired(true)
                    .build();

            TextInput alternativesInput = TextInput.create("alternatives", "Alternatives", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Describe any alternative solutions here...")
                    .setRequired(false)
                    .build();

            Modal featureRequestModal = Modal.create("feature_request_modal", "Feature Request")
                    .addActionRow(titleInput)
                    .addActionRow(descriptionInput)
                    .addActionRow(alternativesInput)
                    .build();
            event.replyModal(featureRequestModal).queue();
        } else if (Objects.equals(event.getButton().getId(), "create_custom_ticket")) {
            // Delete the original message and create a modal for Custom Ticket
            event.getMessage().delete().queue();

            TextInput titleInput = TextInput.create("title", "Title", TextInputStyle.SHORT)
                    .setPlaceholder("Enter the ticket title here...")
                    .setRequired(true)
                    .build();

            TextInput descriptionInput = TextInput.create("description", "Description", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Describe the ticket here...")
                    .setRequired(true)
                    .build();

            Modal customTicketModal = Modal.create("custom_ticket_modal", "Custom Ticket")
                    .addActionRow(titleInput)
                    .addActionRow(descriptionInput)
                    .build();
            event.replyModal(customTicketModal).queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        TicketHandler ticketHandler = new TicketHandler();

        if (event.getModalId().equals("bug_report_modal")) {
            // Retrieve the user's input for Bug Report
            String title = Objects.requireNonNull(event.getValue("title")).getAsString();
            String description = Objects.requireNonNull(event.getValue("description")).getAsString();
            String steps = event.getValue("steps") != null ? Objects.requireNonNull(event.getValue("steps")).getAsString() : "";
            String expected = event.getValue("expected") != null ? Objects.requireNonNull(event.getValue("expected")).getAsString() : "";

            // Proceed to create the ticket using the input
            ticketHandler.startTicketProcess(Objects.requireNonNull(event.getMember()), event.getChannel().asTextChannel(), title, description, steps, expected);

            event.deferEdit().queue();
        } else if (event.getModalId().equals("feature_request_modal")) {
            // Retrieve the user's input for Feature Request
            String title = Objects.requireNonNull(event.getValue("title")).getAsString();
            String description = Objects.requireNonNull(event.getValue("description")).getAsString();
            String alternatives = event.getValue("alternatives") != null ? Objects.requireNonNull(event.getValue("alternatives")).getAsString() : "";

            // Proceed to create the ticket using the input
            ticketHandler.startTicketProcess(Objects.requireNonNull(event.getMember()), event.getChannel().asTextChannel(), title, description, alternatives);

            event.deferEdit().queue();
        } else if (event.getModalId().equals("custom_ticket_modal")) {
            // Retrieve the user's input for Custom Ticket
            String title = Objects.requireNonNull(event.getValue("title")).getAsString();
            String description = Objects.requireNonNull(event.getValue("description")).getAsString();

            // Proceed to create the ticket using the input
            ticketHandler.startTicketProcess(Objects.requireNonNull(event.getMember()), event.getChannel().asTextChannel(), title, description);

            event.deferEdit().queue();
        }
    }
}
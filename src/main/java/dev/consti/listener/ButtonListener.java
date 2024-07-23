package dev.consti.listener;

import dev.consti.ticket.TicketHandler;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.Objects;

public class ButtonListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (Objects.equals(event.getButton().getId(), "create_ticket")) {
            StringSelectMenu menu = StringSelectMenu.create("ticket_type")
                    .setPlaceholder("Choose the type of ticket to create")
                    .addOption("Bug Report", "bug_report")
                    .addOption("Feature Request", "feature_request")
                    .build();
            event.reply("Please choose the type of ticket:").setEphemeral(true).addActionRow(menu).queue();
        } else if (Objects.equals(event.getButton().getId(), "close_ticket")) {
            // Close the ticket
            event.getChannel().delete().queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("ticket_type")) {
            String selected = event.getValues().get(0);
            if (selected.equals("bug_report")) {
                // Create and send a modal for Bug Report
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
            } else if (selected.equals("feature_request")) {
                // Create and send a modal for Feature Request
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
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("bug_report_modal")) {
            // Retrieve the user's input for Bug Report
            String title = Objects.requireNonNull(event.getValue("title")).getAsString();
            String description = Objects.requireNonNull(event.getValue("description")).getAsString();
            String steps = Objects.requireNonNull(event.getValue("steps")).getAsString();
            String expected = Objects.requireNonNull(event.getValue("expected")).getAsString();

            // Proceed to create the ticket using the input
            TicketHandler ticketHandler = new TicketHandler();
            ticketHandler.startTicketProcess(Objects.requireNonNull(event.getMember()), event.getChannel().asTextChannel(), title, description, steps, expected);

            event.reply("Your bug report has been created!").setEphemeral(true).queue();
        } else if (event.getModalId().equals("feature_request_modal")) {
            // Retrieve the user's input for Feature Request
            String title = Objects.requireNonNull(event.getValue("title")).getAsString();
            String description = Objects.requireNonNull(event.getValue("description")).getAsString();
            String alternatives = Objects.requireNonNull(event.getValue("alternatives")).getAsString();

            // Proceed to create the ticket using the input
            TicketHandler ticketHandler = new TicketHandler();
            ticketHandler.startTicketProcess(Objects.requireNonNull(event.getMember()), event.getChannel().asTextChannel(), title, description, alternatives);

            event.reply("Your feature request has been created!").setEphemeral(true).queue();
        }
    }
}

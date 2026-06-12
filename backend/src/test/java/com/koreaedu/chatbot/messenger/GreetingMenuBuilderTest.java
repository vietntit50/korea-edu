package com.koreaedu.chatbot.messenger;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GreetingMenuBuilderTest {

    private final GreetingMenuBuilder builder = new GreetingMenuBuilder();

    @Test
    void exposesSevenJourneyFirstMenuOptions() {
        assertThat(builder.menuOptions()).hasSize(7);
        assertThat(builder.menuOptions())
                .extracting(MenuOption::payload)
                .containsExactly(
                        "MENU_PROCESS",
                        "MENU_GENERAL_COST",
                        "MENU_ELIGIBILITY",
                        "MENU_REGION",
                        "MENU_D4_D2",
                        "MENU_SCHOOL_SEARCH",
                        "MENU_HUMAN_HANDOFF");
    }

    @Test
    void greetingDoesNotAskSchoolFirst() {
        assertThat(builder.greetingText().toLowerCase()).doesNotContain("truong nao");
    }
}

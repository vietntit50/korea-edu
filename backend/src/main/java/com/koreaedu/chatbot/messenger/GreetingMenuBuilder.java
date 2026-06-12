package com.koreaedu.chatbot.messenger;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GreetingMenuBuilder {

    public static final String GREETING_TEXT =
            """
            Chao ban, trung tam co the ho tro ban tim hieu du hoc Han Quoc theo tung buoc: quy trinh, chi phi, check ho so, chon khu vuc va lo trinh D4-1/D2.

            Ban muon tim hieu muc nao truoc?""";

    private static final List<MenuOption> MENU_OPTIONS = List.of(
            new MenuOption("Quy trinh du hoc", "MENU_PROCESS", "ASK_PROCESS"),
            new MenuOption("Tong chi phi", "MENU_GENERAL_COST", "ASK_GENERAL_COST"),
            new MenuOption("Check ho so", "MENU_ELIGIBILITY", "CHECK_ELIGIBILITY"),
            new MenuOption("Seoul hay tinh", "MENU_REGION", "ASK_REGION_ADVICE"),
            new MenuOption("D4-1 va D2", "MENU_D4_D2", "ASK_D4_D2"),
            new MenuOption("Tim truong", "MENU_SCHOOL_SEARCH", "ASK_SCHOOL_RECOMMENDATION"),
            new MenuOption("Gap tu van vien", "MENU_HUMAN_HANDOFF", "HUMAN_HANDOFF"));

    public String greetingText() {
        return GREETING_TEXT;
    }

    public List<MenuOption> menuOptions() {
        return MENU_OPTIONS;
    }

    public String placeholderForPayload(String payload) {
        return MENU_OPTIONS.stream()
                .filter(option -> option.payload().equals(payload))
                .findFirst()
                .map(option -> "Trung tam da ghi nhan ban chon: " + option.label()
                        + ". Phan huong dan chi tiet se duoc cap nhat o Slice 2.")
                .orElse("Trung tam da ghi nhan lua chon cua ban. Phan huong dan chi tiet se duoc cap nhat o Slice 2.");
    }
}

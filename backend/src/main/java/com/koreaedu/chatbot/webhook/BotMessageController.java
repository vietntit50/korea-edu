package com.koreaedu.chatbot.webhook;

import com.koreaedu.chatbot.config.TraceIdFilter;
import com.koreaedu.chatbot.domain.MessageType;
import com.koreaedu.chatbot.messenger.InboundMessage;
import com.koreaedu.chatbot.orchestrator.BotOrchestrator;
import com.koreaedu.chatbot.orchestrator.MessageDedupService;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bot/messages")
@Validated
public class BotMessageController {

    private final MessageDedupService messageDedupService;
    private final BotOrchestrator botOrchestrator;

    public BotMessageController(MessageDedupService messageDedupService, BotOrchestrator botOrchestrator) {
        this.messageDedupService = messageDedupService;
        this.botOrchestrator = botOrchestrator;
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processMessage(@RequestBody @Validated ProcessMessageRequest request) {
        String traceId = request.traceId() != null ? request.traceId() : MDC.get(TraceIdFilter.MDC_TRACE_ID);
        String mid = request.mid() != null ? request.mid() : "local-" + UUID.randomUUID();

        if (messageDedupService.isDuplicate(mid)) {
            return ResponseEntity.ok(Map.of("status", "duplicate_ignored", "trace_id", traceId));
        }

        MessageType messageType = request.payload() != null ? MessageType.QUICK_REPLY : MessageType.TEXT;
        InboundMessage inbound = new InboundMessage(request.psid(), mid, messageType, request.text(), request.payload());
        BotOrchestrator.ProcessResult result = botOrchestrator.processInbound(inbound, traceId);

        return ResponseEntity.ok(Map.of(
                "session_id", result.sessionId().toString(),
                "intent", result.intent() != null ? result.intent() : "",
                "state", result.state(),
                "messages", result.messages(),
                "trace_id", traceId));
    }

    public record ProcessMessageRequest(
            @NotBlank String psid,
            String text,
            String payload,
            String mid,
            String source,
            String traceId) {
    }
}

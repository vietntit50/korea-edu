package com.koreaedu.chatbot.webhook;

import com.koreaedu.chatbot.config.TraceIdFilter;
import com.koreaedu.chatbot.messenger.FacebookSignatureVerifier;
import com.koreaedu.chatbot.messenger.InboundMessage;
import com.koreaedu.chatbot.messenger.MessengerWebhookParser;
import com.koreaedu.chatbot.orchestrator.BotOrchestrator;
import com.koreaedu.chatbot.orchestrator.MessageDedupService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/facebook")
public class FacebookWebhookController {

    private static final Logger log = LoggerFactory.getLogger(FacebookWebhookController.class);

    private final FacebookSignatureVerifier signatureVerifier;
    private final MessengerWebhookParser webhookParser;
    private final MessageDedupService messageDedupService;
    private final BotOrchestrator botOrchestrator;
    private final com.koreaedu.chatbot.config.FacebookProperties facebookProperties;

    public FacebookWebhookController(
            FacebookSignatureVerifier signatureVerifier,
            MessengerWebhookParser webhookParser,
            MessageDedupService messageDedupService,
            BotOrchestrator botOrchestrator,
            com.koreaedu.chatbot.config.FacebookProperties facebookProperties) {
        this.signatureVerifier = signatureVerifier;
        this.webhookParser = webhookParser;
        this.messageDedupService = messageDedupService;
        this.botOrchestrator = botOrchestrator;
        this.facebookProperties = facebookProperties;
    }

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String verifyToken,
            @RequestParam(name = "hub.challenge", required = false) String challenge) {
        if ("subscribe".equals(mode) && facebookProperties.verifyToken().equals(verifyToken)) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> receiveWebhook(
            @RequestBody byte[] rawBody,
            @RequestHeader(name = "X-Hub-Signature-256", required = false) String signature) {
        String traceId = MDC.get(TraceIdFilter.MDC_TRACE_ID);
        if (!signatureVerifier.isValid(signature, rawBody)) {
            log.warn("Invalid Facebook webhook signature traceId={}", traceId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "invalid_signature"));
        }

        String body = new String(rawBody, StandardCharsets.UTF_8);
        List<InboundMessage> inboundMessages = webhookParser.parse(body);
        log.info(
                "Facebook webhook received traceId={} parsedMessages={}",
                traceId,
                inboundMessages.size());
        for (InboundMessage inbound : inboundMessages) {
            if (messageDedupService.isDuplicate(inbound.mid())) {
                log.info("Duplicate Facebook message ignored mid={} traceId={}", inbound.mid(), traceId);
                continue;
            }
            botOrchestrator.processInbound(inbound, traceId);
        }

        return ResponseEntity.ok(Map.of("status", "received"));
    }
}

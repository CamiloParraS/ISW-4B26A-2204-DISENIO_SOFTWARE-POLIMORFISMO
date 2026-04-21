# Class Diagram Review

## What feels off

- `AuditLog` is a cross-cutting concern, not part of the notification domain itself. Keep it only if you need compliance, traceability, or support diagnostics. Otherwise it adds noise and couples the model to persistence history.
- `recipientRole` in `Notification` is too rigid. A notification should represent a single recipient, not a role bucket. Role-based routing belongs in recipient selection, before the notification exists.
- `UserRole` is only worth keeping if roles are used elsewhere, like authorization or business workflows. For delivery alone, routing to an explicit recipient list is cleaner and easier to evolve.
- `code` on `Notification` overlaps with `NotificationTemplate.code`. That makes the model harder to read. One identifier is usually enough unless both values have different business meanings.
- `NotificationService` is doing the right kind of orchestration, but it should stay the only place that coordinates template resolution, recipient filtering, creation, and dispatch.

## Suggested direction

- Keep notifications per recipient.
- Resolve the eligible users first, then create one notification per user.
- Keep audit as an infrastructure concern if you still need it.
- Keep roles only if they serve another part of the system. If they exist only for notification routing, replace them with a candidate recipient list.

```mermaid
classDiagram
direction LR

class NotificationChannel {
  <<enumeration>>
  EMAIL
  SMS
  PUSH
}

class NotificationType {
  <<enumeration>>
  GRADE_PUBLISHED
  TUITION_REMINDER
  CLASS_CANCELLED
  EVENT_CONFIRMATION
}

class NotificationStatus {
  <<enumeration>>
  PENDING
  SENT
  FAILED
  RETRYING
  CANCELLED
}

class Criticality {
  <<enumeration>>
  HIGH
  LOW
}

class Notification {
  <<abstract>>
  +id: String
  +recipientId: String
  +resolvedMessage: String
  +dateSent: LocalDateTime
  +status: NotificationStatus
  +channel: NotificationChannel
  +criticality: Criticality
  +createdAt: LocalDateTime
  +send(): void
  #buildLogMessage(): String
}

class EmailNotification {
  +toAddress: String
  +subject: String
  +htmlBody: String
  +attachments: List~String~
  +send(): void
}

class SmsNotification {
  +phoneNumber: String
  +characterCount: int
  +provider: String
  +send(): void
}

class PushNotification {
  +deviceToken: String
  +platform: String
  +deepLinkUrl: String
  +iconUrl: String
  +send(): void
}

class User {
  +id: String
  +name: String
  +email: String
  +phone: String
  +deviceToken: String
}

class NotificationTemplate {
  +id: String
  +code: String
  +channel: NotificationChannel
  +subject: String
  +bodyTemplate: String
}

class NotificationTypeConfig {
  +notificationType: NotificationType
  +channel: NotificationChannel
  +enabled: boolean
  +criticality: Criticality
  +allowOptOut: boolean
}

class UserOptOut {
  +userId: String
  +notificationType: NotificationType
}

class NotificationRetry {
  +id: String
  +notificationId: String
  +attemptNumber: int
  +attemptedAt: LocalDateTime
  +errorMessage: String
}

class TemplateEngine {
  +resolve(template: NotificationTemplate, placeholders: Map~String, String~): String
}

class NotificationFactory {
  -templateEngine: TemplateEngine
  +create(type: NotificationType, recipient: User, template: NotificationTemplate, placeholders: Map~String, String~, config: NotificationTypeConfig): Notification
  -createEmailNotification(recipient: User, template: NotificationTemplate, resolvedMsg: String, criticality: Criticality): EmailNotification
  -createSmsNotification(recipient: User, template: NotificationTemplate, resolvedMsg: String, criticality: Criticality): SmsNotification
  -createPushNotification(recipient: User, template: NotificationTemplate, resolvedMsg: String, criticality: Criticality): PushNotification
}

class RecipientResolver {
  -users: List~User~
  -optOuts: List~UserOptOut~
  +resolve(type: NotificationType, candidates: List~User~): List~User~
  -isOptedOut(userId: String, type: NotificationType): boolean
}

class RetryService {
  -maxRetries: int
  -retryLog: List~NotificationRetry~
  +retry(notification: Notification): void
  +shouldRetry(notification: Notification): boolean
  -logAttempt(notificationId: String, errorMessage: String): void
}

class NotificationService {
  -factory: NotificationFactory
  -recipientResolver: RecipientResolver
  -retryService: RetryService
  +trigger(type: NotificationType, candidates: List~User~, placeholders: Map~String, String~): void
  -dispatch(notification: Notification): void
  -handleFailure(notification: Notification): void
}

Notification <|-- EmailNotification
Notification <|-- SmsNotification
Notification <|-- PushNotification

Notification --> NotificationStatus
Notification --> NotificationChannel
Notification --> Criticality
NotificationTemplate --> NotificationChannel
NotificationTypeConfig --> NotificationType
NotificationTypeConfig --> NotificationChannel
NotificationTypeConfig --> Criticality
UserOptOut --> NotificationType
NotificationRetry --> Notification

NotificationService *-- NotificationFactory
NotificationService *-- RecipientResolver
NotificationService *-- RetryService
NotificationFactory *-- TemplateEngine
NotificationFactory ..> NotificationTemplate
NotificationFactory ..> NotificationTypeConfig
RecipientResolver ..> User
RecipientResolver ..> UserOptOut
```

If you still need audit, add it as a separate infrastructure service instead of baking it into the core notification model. If you later need role-based targeting, model that as an audience rule or recipient selector rather than attaching roles directly to the notification itself.

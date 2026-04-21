# DiagramaClase V2

This version keeps the original architecture but clarifies boundaries, removes overlap, and adds explicit multiplicities in relationships.

## Modeling Rules (explicit)

- NotificationTypeConfig decides policy: enabled/disabled, criticality, and opt-out permissions.
- NotificationTemplate decides content: subject/body structure for a channel.
- A Notification is always created per recipient (not per role).
- Roles are used for routing rules, not as the notification target itself.
- Audit logs record meaningful business events, not every internal method call.

## Enumerations

«enumeration» NotificationChannel
EMAIL
SMS
PUSH

«enumeration» NotificationType
GRADE_PUBLISHED
TUITION_REMINDER
CLASS_CANCELLED
EVENT_CONFIRMATION

«enumeration» NotificationStatus
PENDING
SENT
FAILED
RETRYING
CANCELLED

«enumeration» Criticality
HIGH
LOW

«enumeration» UserRole
STUDENT
PROFESSOR
ADMIN_STAFF

«enumeration» AuditEventType
NOTIFICATION_TRIGGERED
NOTIFICATION_SENT
NOTIFICATION_FAILED
RETRY_SCHEDULED
CONFIG_CHANGED
OPTOUT_CHANGED

## Domain Classes

«abstract» Notification

- id : String
- type : NotificationType
- recipientId : String
- resolvedMessage : String
- status : NotificationStatus
- criticality : Criticality
- createdAt : LocalDateTime
- sentAt : LocalDateTime
- attemptCount : int
- lastError : String

* send() : void
* getId() : String
* getType() : NotificationType
* getRecipientId() : String
* getStatus() : NotificationStatus
* getCriticality() : Criticality
* getResolvedMessage() : String
* getAttemptCount() : int
* markSent(sentAt : LocalDateTime) : void
* markFailed(errorMessage : String) : void
* canRetry(maxRetries : int) : boolean

EmailNotification (extends Notification)

- toAddress : String
- subject : String
- htmlBody : String
- attachments : List<String>

* send() : void
* getToAddress() : String
* getSubject() : String
* getHtmlBody() : String
* getAttachments() : List<String>

SmsNotification (extends Notification)

- phoneNumber : String
- characterCount : int
- provider : String

* send() : void
* getPhoneNumber() : String
* getCharacterCount() : int
* getProvider() : String

PushNotification (extends Notification)

- deviceToken : String
- platform : String
- deepLinkUrl : String
- iconUrl : String

* send() : void
* getDeviceToken() : String
* getPlatform() : String
* getDeepLinkUrl() : String
* getIconUrl() : String

User

- id : String
- name : String
- email : String
- phone : String
- role : UserRole
- deviceToken : String

* getId() : String
* getName() : String
* getEmail() : String
* getPhone() : String
* getRole() : UserRole
* getDeviceToken() : String

NotificationTemplate

- id : String
- code : String
- channel : NotificationChannel
- subject : String
- bodyTemplate : String

* getId() : String
* getCode() : String
* getChannel() : NotificationChannel
* getSubject() : String
* getBodyTemplate() : String

NotificationTypeConfig

- notificationType : NotificationType
- channel : NotificationChannel
- enabled : boolean
- criticality : Criticality
- allowOptOut : boolean

* isEnabled() : boolean
* getCriticality() : Criticality
* getNotificationType() : NotificationType
* getChannel() : NotificationChannel
* isAllowOptOut() : boolean

UserOptOut

- userId : String
- notificationType : NotificationType
- channel : NotificationChannel

* getUserId() : String
* getNotificationType() : NotificationType
* getChannel() : NotificationChannel

NotificationRetry

- id : String
- notificationId : String
- attemptNumber : int
- attemptedAt : LocalDateTime
- nextAttemptAt : LocalDateTime
- errorMessage : String

* getId() : String
* getAttemptNumber() : int
* getErrorMessage() : String
* getNextAttemptAt() : LocalDateTime

AuditLog

- id : String
- eventType : AuditEventType
- entityType : String
- entityId : String
- actorId : String
- previousValue : String
- newValue : String
- timestamp : LocalDateTime

* getId() : String
* getEventType() : AuditEventType
* getTimestamp() : LocalDateTime

## Service Classes

TemplateEngine

- resolve(template : NotificationTemplate, placeholders : Map<String, String>) : String

NotificationFactory

- templateEngine : TemplateEngine

* create(type : NotificationType, recipient : User, template : NotificationTemplate, placeholders : Map<String, String>, config : NotificationTypeConfig) : Notification

- createEmailNotification(recipient : User, template : NotificationTemplate, resolvedMsg : String, criticality : Criticality) : EmailNotification
- createSmsNotification(recipient : User, template : NotificationTemplate, resolvedMsg : String, criticality : Criticality) : SmsNotification
- createPushNotification(recipient : User, template : NotificationTemplate, resolvedMsg : String, criticality : Criticality) : PushNotification

RecipientResolver

- users : List<User>
- optOuts : List<UserOptOut>

* resolve(type : NotificationType, role : UserRole, channel : NotificationChannel) : List<User>

- isOptedOut(userId : String, type : NotificationType, channel : NotificationChannel) : boolean

RetryService

- maxRetries : int
- retryLog : List<NotificationRetry>

* retry(notification : Notification) : void
* shouldRetry(notification : Notification) : boolean

- logAttempt(notificationId : String, errorMessage : String, nextAttemptAt : LocalDateTime) : void

AuditService

- logs : List<AuditLog>

* log(eventType : AuditEventType, entityType : String, entityId : String, previousValue : String, newValue : String, actorId : String) : void
* getLogs() : List<AuditLog>

NotificationService (main orchestrator)

- factory : NotificationFactory
- recipientResolver : RecipientResolver
- auditService : AuditService
- retryService : RetryService

* trigger(type : NotificationType, targetRole : UserRole, placeholders : Map<String, String>) : void

- dispatch(notification : Notification) : void
- handleFailure(notification : Notification) : void

## Relationships (with multiplicity)

- EmailNotification --|> Notification
- SmsNotification --|> Notification
- PushNotification --|> Notification

- Notification "0..\*" --> "1" User : recipient
- Notification "0..\*" --> "1" NotificationType : typed as
- Notification "0..\*" --> "1" NotificationStatus : current status
- Notification "0..\*" --> "1" Criticality : priority

- User "0..\*" --> "1" UserRole : has role

- NotificationTemplate "0..\*" --> "1" NotificationChannel : rendered for channel
- NotificationTypeConfig "0..\*" --> "1" NotificationType : policy for type
- NotificationTypeConfig "0..\*" --> "1" NotificationChannel : policy for channel
- NotificationTypeConfig "0..\*" --> "1" Criticality : default criticality

- UserOptOut "0..\*" --> "1" User : owned by user
- UserOptOut "0..\*" --> "1" NotificationType : opt-out for type
- UserOptOut "0..\*" --> "1" NotificationChannel : opt-out for channel

- NotificationRetry "0..\*" --> "1" Notification : attempts of
- AuditLog "0..\*" --> "1" AuditEventType : event category

- NotificationService "1" \*-- "1" NotificationFactory : owns instance
- NotificationService "1" \*-- "1" RecipientResolver : owns instance
- NotificationService "1" --> "1" AuditService : uses
- NotificationService "1" --> "1" RetryService : uses

- NotificationFactory "1" \*-- "1" TemplateEngine : owns instance
- NotificationFactory "1" ..> "1" NotificationTemplate : uses
- NotificationFactory "1" ..> "1" NotificationTypeConfig : uses
- NotificationFactory "1" ..> "1" Notification : creates

- RecipientResolver "1" --> "0..\*" User : navigates list
- RecipientResolver "1" --> "0..\*" UserOptOut : navigates list

- RetryService "1" --> "0..\*" NotificationRetry : creates and tracks
- AuditService "1" --> "0..\*" AuditLog : creates and tracks

## Notes

- If you need immutable history, keep recipient role as snapshot in AuditLog metadata rather than in Notification.
- If your domain requires per-user rules beyond roles, add a separate eligibility policy object later without removing roles.

# DiagramaClase

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

## Domain Classes

«abstract» Notification
id : String
code : String
recipientId : String
recipientRole : UserRole
resolvedMessage : String
dateSent : LocalDateTime
status : NotificationStatus
channel : NotificationChannel
criticality : Criticality
createdAt : LocalDateTime

- «abstract» send() : void ← polymorphism hook
- getId() : String
- getStatus() : NotificationStatus
- setStatus(status : NotificationStatus) : void
- getChannel() : NotificationChannel
- getCriticality() : Criticality
- getResolvedMessage() : String
- getRecipientId() : String

'#' buildLogMessage() : String

EmailNotification (extends Notification)

- toAddress : String
- subject : String
- htmlBody : String
- attachments : List<String>

* send() : void ← overrides Notification
* getToAddress() : String
* getSubject() : String
* getHtmlBody() : String
* getAttachments() : List<String>

SmsNotification (extends Notification)

- phoneNumber : String
- characterCount : int
- provider : String

* send() : void ← overrides Notification
* getPhoneNumber() : String
* getCharacterCount() : int
* getProvider() : String

PushNotification (extends Notification)

- deviceToken : String
- platform : String
- deepLinkUrl : String
- iconUrl : String

* send() : void ← overrides Notification
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

* getUserId() : String
* getNotificationType() : NotificationType

NotificationRetry

- id : String
- notificationId : String
- attemptNumber : int
- attemptedAt : LocalDateTime
- errorMessage : String

* getId() : String
* getAttemptNumber() : int
* getErrorMessage() : String

AuditLog

- id : String
- entityType : String
- entityId : String
- action : String
- actorId : String
- previousValue : String
- newValue : String
- timestamp : LocalDateTime

* getId() : String
* getAction() : String
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

* resolve(type : NotificationType, role : UserRole) : List<User>

- isOptedOut(userId : String, type : NotificationType) : boolean

RetryService

- maxRetries : int
- retryLog : List<NotificationRetry>

* retry(notification : Notification) : void
* shouldRetry(notification : Notification) : boolean

- logAttempt(notificationId : String, errorMessage : String) : void

AuditService

- logs : List<AuditLog>

* log(entityType : String, entityId : String, action : String, previousValue : String, newValue : String, actorId : String) : void
* getLogs() : List<AuditLog>

NotificationService (main orchestrator)

- factory : NotificationFactory
- recipientResolver : RecipientResolver
- auditService : AuditService
- retryService : RetryService

* trigger(type : NotificationType, placeholders : Map<String, String>) : void

- dispatch(notification : Notification) : void
- handleFailure(notification : Notification) : void

Relationships
FromRelationshipToDetailEmailNotificationGeneralization (extends)NotificationConcrete subclassSmsNotificationGeneralization (extends)NotificationConcrete subclassPushNotificationGeneralization (extends)NotificationConcrete subclassNotificationAssociationNotificationStatususes (enum field)NotificationAssociationNotificationChanneluses (enum field)NotificationAssociationCriticalityuses (enum field)UserAssociationUserRoleuses (enum field)NotificationServiceCompositionNotificationFactoryowns an instanceNotificationServiceCompositionRecipientResolverowns an instanceNotificationServiceAssociationAuditServiceusesNotificationServiceAssociationRetryServiceusesNotificationFactoryCompositionTemplateEngineowns an instanceNotificationFactoryDependencyNotification«creates»RecipientResolverAssociationUsernavigates a listRecipientResolverAssociationUserOptOutnavigates a listRetryServiceAssociationNotificationRetry«creates» and tracksAuditServiceAssociationAuditLog«creates» and tracksNotificationTypeConfigDependencyNotificationTypeuses (enum field)NotificationTypeConfigDependencyNotificationChanneluses (enum field)UserOptOutDependencyNotificationTypeuses (enum field)

ServicioNotificaciones
Attributes

- factory : NotificacionFactory
- procesadorDestinatario : ProcesadorDestinatario
- servicioReintentos : ServicioReintentos
  Methods

* trigger(tipo: NotificacionTipo, rolObjetivo: RolUsuario, placeholders: Map<String,String>) : void

- despachar(notificacion: Notificacion) : void
- manejarFallo(notificacion: Notificacion) : void
  ServicioReintentos
  Attributes
- maxReintentos : int
- logReintentos : List<ReintentoNotificacion>
  Methods

* reintentar(notificacion: Notificacion) : void
* deberiaReintentar(notificacion: Notificacion) : boolean

- registrarIntento(idNotificacion: String, mensajeError: String) : void
  ProcesadorDestinatario
  Attributes
- usuarios : List<Usuario>
- exclusiones : List<ExclusionUsuario>
  Methods

* procesar(tipo: NotificacionTipo, rol: RolUsuario) : List<User>
* estaExcluido(idUsuario: String, tipo: NotificacionTipo) : boolean
  NotificacionFactory
  Attributes

- motorPlantilla : MotorPlantilla
  Methods

* crear(tipo: TipoNotificacion, destinatario: Usuario, plantilla: PlantillaNotificacion, placeholders: Map<String,String>, config: ConfigTipoNotificacion) : Notificacion

- crearNotificacionEmail(destinatario: Usuario, plantilla: PlantillaNotificacion, msjProcesado: String, criticalidad: Criticalidad) : EmailNotificacion
- crearSMSNotificacion(destinatario: Usuario, plantilla: PlantillaNotificacion, msjProcesado: String, criticalidad: Criticalidad) : SMSNotificacion
- crearPushNotificacion(destinatario: Usuario, plantilla: PlantillaNotificacion, msjProcesado: String, criticalidad: Criticalidad) : PushNotificacion
  MotorPlantilla
  Methods

* procesar(plantilla: PlantillaNotificacion, placeholders: Map<String,String>) : String
  abstract
  Notificacion
  Attributes

- codigo : String
- tipo : TipoNotificacion
- idDestinatario : String
- mensaje : String
- fechaEnvio : LocalDateTime
- estado : EstadoNotificacion
- criticalidad : Criticalidad
- enviadoEn : LocalDateTime
- contadorIntento : int
- ultimoError : String
  Methods

* <abstract> enviar() : void
* getId() : String
* getTipoNotificacion() : TipoNotificacion
* getEstado() : EstadoNotificacion
* setEstado(estado: EstadoNotificacion) : void
* getCanal() : CanalNotificacion
* getCriticalidad() : Criticalidad
* getMensaje() : String
* getIdDestinatario() : String
* getContadorIntento() : int
* marcarEnviado(enviadoEn: LocalDateTime) : void
* marcarFallido(mensajeError: String) : void
* puedeReintentar(maximosReintentos: int) : boolean
  extends Notificacion
  EmailNotificacion
  Attributes

- direccionDestinatario : String
- asunto : String
- cuerpoHTML : String
- adjuntos : List<String>
  Methods

* enviar() : void ← overrides Notificacion
* getDireccionDestinatario() : String
* getAsunto() : String
* getCuerpoHTML() : String
* getAdjuntos() : List<String>
  extends Notificacion
  SMSNotificacion
  Attributes

- numeroTelefono : String
- numCaracteres : int
- proveedor : String
  Methods

* enviar() : void ← overrides Notificacion
* getNumeroTelefono() : String
* getNumeroCaracteres() : int
* getProveedor() : String
  extends Notificacion
  PushNotificacion
  Attributes

- tokenDispositivo : String
- plataforma : String
- deepLinkUrl : String
- iconURL : String
  Methods

* enviar() : void ← overrides Notificacion
* getTokenDispositivo() : String
* getPlataforma() : String
* getDeepLinkURL() : String
* getIconUrl() : String
  PlantillaNotificacion
  Attributes

- id : String
- codigo : String
- canal : CanalNotificacion
- asunto : String
- plantillaCuerpo : String
  Methods

* getId() : String
* getCodigo() : String
* getCanal() : CanalNotificacion
* getAsunto() : String
* getPlantillaCuerpo() : String
  Usuario
  Attributes

- id : String
- nombre : String
- email : String
- telefono : String
- rol : RolUsuario
- tokenDispositivo : String
  Methods

* getId() : String
* getNombre() : String
* getEmail() : String
* getTelefono() : String
* getRol() : RolUsuario
* getTokenDispositivo() : String
  ExclusionUsuario
  Attributes

- idUsuario : String
- tipoNotificacion : TipoNotificacion
- canal : CanalNotificacion
  Methods

* getIdUsuario() : String
* getTipoNotificacion() : TipoNotificacion
* getCanal() : CanalNotificacion
  ConfigTipoNotificacion
  Attributes

- tipoNotificacion : TipoNotificacion
- canal : CanalNotificacion
- activo : boolean
- criticalidad : Criticalidad
- permitirDesuscripcion : boolean
  Methods

* estaHabilitado() : boolean
* getCriticalidad() : Criticalidad
* getTipoNotificacion() : TipoNotificacion
* getCanal() : CanalNotificacion
* permiteDesuscripcion() : boolean
  <<enum>>
  CanalNotificacion
  Values
  EMAIL
  SMS
  PUSH
  <<enum>>
  TipoNotificacion
  Values
  NOTA_PUBLICADA
  RECORDATORIO_MATRICULA
  CLASE_CANCELADA
  CONFIRMACION_EVENTO
  <<enum>>
  RolUsuario
  Values
  ESTUDIANTE
  PROFESOR
  ADMIN
  <<enum>>
  EstadoNotificacion
  Values
  PENDIENTE
  ENVIADA
  FALLIDA
  REINTENTANDO
  CANCELADA

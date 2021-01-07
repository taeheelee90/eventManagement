package com.taeheelee.eventmanagement.modules.event.registrationPublisher;



public class RegistrationEventListener {
	
	/*
	 * @Slf4j
@Async
@Transactional
@RequiredArgsConstructor
@Component
public class EnrollmentEventListener {

	private final NotificationRepository notificationRepository;
	private final AppProperties appProperties;
	private final TemplateEngine templateEngine;
	private final EmailService emailService;
	
	@EventListener
	public void listenEnrollment(EnrollmentEvent enrollmentEvent) {
		Enrollment enrollment = enrollmentEvent.getEnrollment();
		Account account = enrollment.getAccount();
		Activity activity = enrollment.getActivity();
		Event event = activity.getEvent();
		
		if(account.isEventEnrollmentByEmail()) {
			sendEmail(enrollmentEvent, account, activity, event);
		}
		
		if(account.isEventUpdateByWeb()) {
			sendNotification(enrollmentEvent, account, activity, event);
		}
	}

	private void sendNotification(EnrollmentEvent enrollmentEvent, Account account, Activity activity, Event event) {
		Notification notification = new Notification();
		notification.setTitle(event.getTitle() + " / " + activity.getTitle());
		notification.setLink("/event/" + event.getEncodedPath() + "/activities/" + activity.getId());
		notification.setChecked(false);
		notification.setCreatedAt(LocalDateTime.now());
		notification.setMessage(enrollmentEvent.getMessage());
		notification.setAccount(account);
		notification.setNotificationType(NotificationType.ACTIVITY_ENROLLMENT);
		notificationRepository.save(notification);
		
	}

	private void sendEmail(EnrollmentEvent enrollmentEvent, Account account, Activity activity, Event event) {
		Context context = new Context();
		context.setVariable("nickname", account.getNickname());
		context.setVariable("link", "/event/" + event.getEncodedPath() + "/activities/" + activity.getId());
		context.setVariable("linkName", event.getTitle());
		context.setVariable("message", enrollmentEvent.getMessage());
		context.setVariable("host", appProperties.getHost());
		String emailMsg = templateEngine.process("mail/simple-link", context);
		
		EmailMessage emailMessage = EmailMessage.builder()
									.subject("[Event Management] " + activity.getTitle() + " | enrollment result")
									.to(account.getEmail())
									.message(emailMsg)
									.build();
		
		emailService.sendEmail(emailMessage);
		
	}
}
	 * 
	 * */

}

package com.faculty.eventmanagement.concurrency;

import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.model.Registration;
import com.faculty.eventmanagement.model.User;
import com.faculty.eventmanagement.repository.EventRepository;
import com.faculty.eventmanagement.repository.RegistrationRepository;
import com.faculty.eventmanagement.template.EventRegistrationTemplate;
import com.faculty.eventmanagement.template.StandardEventRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class ThreadSafeRegistrationService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    private final ConcurrentHashMap<Long, ReentrantLock> eventLocks
            = new ConcurrentHashMap<>();

    public Registration registerSafely(User user, Event event) {

        ReentrantLock lock = eventLocks.computeIfAbsent(
                event.getId(), id -> new ReentrantLock());

        lock.lock();
        try {
            System.out.println("[THREAD SAFE] Thread "
                    + Thread.currentThread().getName()
                    + " acquired lock for event: " + event.getId());

            Event freshEvent = eventRepository.findById(event.getId())
                    .orElseThrow(() -> new RuntimeException("Event not found"));

            if (freshEvent.getCurrentAttendees()
                    >= freshEvent.getMaxAttendees()) {
                throw new RuntimeException(
                        "Event is fully booked: " + freshEvent.getTitle());
            }

            if (registrationRepository.existsByUserIdAndEventId(
                    user.getId(), freshEvent.getId())) {
                throw new RuntimeException(
                        "User is already registered for this event: "
                                + freshEvent.getTitle());
            }

            EventRegistrationTemplate template
                    = new StandardEventRegistration();
            Registration registration = template.register(user, freshEvent);

            eventRepository.save(freshEvent);
            Registration saved = registrationRepository.save(registration);

            System.out.println("[THREAD SAFE] Registration successful. "
                    + "Seats remaining: "
                    + (freshEvent.getMaxAttendees()
                    - freshEvent.getCurrentAttendees()));

            return saved;

        } finally {
            lock.unlock();
            System.out.println("[THREAD SAFE] Lock released for event: "
                    + event.getId());
        }
    }


    public void removeLock(Long eventId) {
        eventLocks.remove(eventId);
    }
}
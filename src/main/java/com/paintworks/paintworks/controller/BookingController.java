package com.paintworks.paintworks.controller;

import com.paintworks.paintworks.model.Booking;
import com.paintworks.paintworks.model.Painter;
import com.paintworks.paintworks.model.User;
import com.paintworks.paintworks.repository.BookingRepository;
import com.paintworks.paintworks.repository.PainterRepository;
import com.paintworks.paintworks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
@CrossOrigin(origins = "http://localhost:3000")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PainterRepository painterRepository;

    // ✅ Public endpoint - Fetch all bookings (optional)
    @GetMapping("/all")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    // ✅ Secure - Get bookings for logged-in user
    @GetMapping
    public ResponseEntity<List<Booking>> getUserBookings(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).build();
        }

        List<Booking> userBookings = bookingRepository.findByUserId(user.getId());
        return ResponseEntity.ok(userBookings);
    }

    // ✅ Create booking with user and painter linking
    @PostMapping
    public ResponseEntity<Booking> createBooking(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody Booking booking) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).build();
        }

        Painter painter = null;
        if (booking.getPainter() != null && booking.getPainter().getId() != null) {
            painter = painterRepository.findById(booking.getPainter().getId()).orElse(null);
        }

        if (painter == null) {
            return ResponseEntity.badRequest().build(); // painterId not found
        }

        booking.setUser(user);
        booking.setPainter(painter);
        Booking savedBooking = bookingRepository.save(booking);
        return ResponseEntity.ok(savedBooking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null || !booking.getUser().getEmail().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).build();
        }

        bookingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

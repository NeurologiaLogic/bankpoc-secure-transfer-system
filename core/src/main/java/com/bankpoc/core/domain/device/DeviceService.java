package com.bankpoc.core.domain.device;

import com.bankpoc.core.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    /**
     * Checks for an existing device or registers a new one upon successful login.
     * This method ensures the last_login_at timestamp is always updated.
     *
     * @param user The authenticated user.
     * @param deviceId The unique client identifier for the device.
     * @param ipAddress The IP address the login originated from.
     * @param deviceName Optional client-provided device name.
     * @return The Device entity (either existing or newly created).
     */
    @Transactional // Ensures the save/update is atomic
    public Device handleLogin(User user, String deviceId, String deviceName,String ipAddress) {

        Optional<Device> existingDevice = deviceRepository.findByUserIdAndDeviceId(user, deviceId);

        Device device;

        if (existingDevice.isPresent()) {
            // Case 1: Device is known (update last login)
            device = existingDevice.get();
            device.setIpAddress(ipAddress);
            device.setLastLoginAt(Instant.now());
            // Note: is_trusted is usually updated in a separate MFA/security step
        } else {
            // Case 2: New Device (create and mark as untrusted by default)
            device = Device.builder()
                    .user(user)
                    .deviceId(deviceId)
                    .deviceName(deviceName)
                    .ipAddress(ipAddress)
                    .isTrusted(false) // Default is false, requiring security check
                    .build();

            // The @PrePersist hook will set firstLoginAt and lastLoginAt
        }

        return deviceRepository.save(device);
    }

    /**
     * Updates a device's trust status (e.g., after an OTP verification).
     * @param device The device to update.
     * @param isTrusted The new trust status.
     * @return The updated Device entity.
     */
    @Transactional
    public Device updateTrustStatus(Device device, boolean isTrusted) {
        device.setIsTrusted(isTrusted);
        return deviceRepository.save(device);
    }
}
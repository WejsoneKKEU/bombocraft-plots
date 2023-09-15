package com.eternalcode.plots.good.member;

import java.util.UUID;

public record Member(UUID memberId, UUID plotId, UUID userId, String role) {
}
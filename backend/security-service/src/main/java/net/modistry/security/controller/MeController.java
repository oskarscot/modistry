package net.modistry.security.controller;

import net.modistry.security.identity.AccountProfile;
import net.modistry.security.identity.AccountQueryService;
import net.modistry.security.identity.LocalAccountPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class MeController {

    // TODO: change me
    private final AccountQueryService accountQueryService;

    MeController(AccountQueryService accountQueryService) {
        this.accountQueryService = accountQueryService;
    }

    @GetMapping("/me")
    public AccountProfile me(@AuthenticationPrincipal LocalAccountPrincipal principal) {
        return accountQueryService.getAccount(principal.getAccountId());
    }
}

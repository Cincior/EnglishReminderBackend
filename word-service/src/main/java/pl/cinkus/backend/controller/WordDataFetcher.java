package pl.cinkus.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import pl.cinkus.backend.codegen.types.AddWordResult;
import pl.cinkus.backend.codegen.types.InputWordData;
import pl.cinkus.backend.codegen.types.SetLearnedInput;
import pl.cinkus.backend.codegen.types.UpdateWordInput;
import pl.cinkus.backend.dto.WordDataDTO;
import pl.cinkus.backend.service.WordDataService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WordDataFetcher {
    private final WordDataService wordDataService;

    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public List<WordDataDTO> getUserWords() {
        return wordDataService.getUserWords(getUserId());
    }

    @MutationMapping
    @PreAuthorize("hasRole('USER')")
    public AddWordResult addWord(@Argument(name = "input") InputWordData inputWordData) {
        return wordDataService.addWord(inputWordData, getUserId());
    }

    @MutationMapping
    @PreAuthorize("hasRole('USER')")
    public boolean setLearned(@Argument(name = "setLearnedInput") SetLearnedInput setLearnedInput) {
        wordDataService.setLearned(setLearnedInput.getWordId(), getUserId(), setLearnedInput.getIsLearned());
        return true;
    }

    @MutationMapping
    @PreAuthorize("hasRole('USER')")
    public boolean updateWord(@Argument(name = "input") UpdateWordInput input) {
        wordDataService.updateWord(input, getUserId());
        return true;
    }

    @MutationMapping
    @PreAuthorize("hasRole('USER')")
    public boolean deleteWord(@Argument String wordId) {
        wordDataService.deleteWord(wordId, getUserId());
        return true;
    }

    private String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}

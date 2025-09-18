package com.vcampus.client.controller;

import javafx.fxml.FXML;
import java.util.function.Consumer;

public class AcademicChoiceController {

    private Consumer<String> onChoiceMadeCallback;

    public void setOnChoiceMade(Consumer<String> callback) {
        this.onChoiceMadeCallback = callback;
    }

    @FXML
    private void handleArxivChoice() {
        if (onChoiceMadeCallback != null) onChoiceMadeCallback.accept("arXiv");
    }

    @FXML
    private void handleOpenReviewChoice() {
        if (onChoiceMadeCallback != null) onChoiceMadeCallback.accept("OpenReview");
    }

    @FXML
    private void handleGoogleScholarChoice() {
        if (onChoiceMadeCallback != null) onChoiceMadeCallback.accept("GoogleScholar");
    }

    @FXML
    private void handleZLibraryChoice() {
        if (onChoiceMadeCallback != null) onChoiceMadeCallback.accept("ZLibrary");
    }

    /**
     * 【新增】处理 ACM Digital Library 按钮的点击事件
     */
    @FXML
    private void handleAcmChoice() {
        if (onChoiceMadeCallback != null) onChoiceMadeCallback.accept("ACM");
    }
}
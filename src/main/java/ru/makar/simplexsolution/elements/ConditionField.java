package ru.makar.simplexsolution.elements;

import javafx.animation.TranslateTransition;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.UUID;

public class ConditionField extends TextField {

    private static final double DURATION = 500;

    private final UUID id;

    public ConditionField() {
        super();
        this.id = UUID.randomUUID();
        setContextMenu(new ContextMenu());
    }

    private UUID getIdField() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConditionField && this.id.equals(((ConditionField) obj).getIdField());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public void playEntryAnimation() {
        TranslateTransition animation = new TranslateTransition(Duration.millis(DURATION), this);
        animation.setFromX(-170);
        animation.setByX(175);
        animation.play();
    }

    public void playExitAnimation() {
        TranslateTransition animation = new TranslateTransition(Duration.millis(DURATION), this);
        animation.setByX(-190);
        animation.setOnFinished((e) -> {
            ((Pane) this.getParent()).getChildren().remove(this);
        });
        animation.play();
    }
}
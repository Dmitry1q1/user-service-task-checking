package com.mit.userservice.taskchecking.model;

public class JavaTaskChecker extends ProgrammingLanguageTaskChecker {
    @Override
    public Action createAction() {
        return new JavaAction();
    }
}

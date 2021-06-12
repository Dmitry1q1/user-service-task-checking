package com.mit.userservice.taskchecking.model;

public class CPlusPlusTaskChecker extends ProgrammingLanguageTaskChecker {
    @Override
    public Action createAction() {
        return new CPlusPlusAction();
    }
}

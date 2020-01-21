package com.mycompany.pokemonBattle;

interface Menu {
    public void draw();
    public void move(String dir);
    public String getAction();
    public void setButtonText(String txt);
	public void updateForm(String newForm);
	public void typingHandler(String code, String text, boolean shifted);
	public String[] getForms();
}
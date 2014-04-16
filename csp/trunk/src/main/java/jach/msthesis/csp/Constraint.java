package jach.msthesis.csp;

public interface Constraint {
	public boolean isSatisfied();
	public void setCSP(CSP csp);
}

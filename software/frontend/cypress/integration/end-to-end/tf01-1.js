/* eslint-disable no-undef */
/*
/TF01-1/ Anmeldung eines registrierten Users

Anwendungsfall		[LF01-1]

Beschreibung
Folgende Schritte sind auszuführen:

1. https:https://www.iotdevicetracker.software/auth/login aufrufen
2. Username eingeben
3. Passwort korrekt eingeben
4. Anmelden klicken

Erwartetes Ergebnis:
Der User ist erfolgreich angemeldet und befindet sich auf dem Dashboard
*/

describe('/TF01-1/', () => {
	it('Anmeldung eines registrierten Users', () => {
		/* IoT Device Tracker Website aufrufen */
		cy.visit('https://www.iotdevicetracker.software')
		cy.get('button').contains('Login').click()
		cy.url().should('include', '/auth/login')

		/* Username und Passwort des Test-Users eingeben */
		cy.get('input[id="login"]').type('Cypress')
		cy.get('input[id="password"]').type('cypress123')

		/* Auf Sign-in klicken und verifizieren, dass das Dashboard geladen wurde */
		cy.get('button').get('span').contains('Sign in').click()
		cy.get('h3').should('contain', 'My Device Groups')
	})
})

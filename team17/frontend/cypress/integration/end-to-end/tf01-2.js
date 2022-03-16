/* eslint-disable no-undef */
/*
/TF01-2/ Abmeldung eines Users

Anwendungsfall		[LF01-2]

Beschreibung
Folgende Schritte sind auszuführen:

1. Der User muss eingeloggt sein
2. Auf den Benutzernamen oben rechts klicken
3. "Logout" auswählen

Erwartetes Ergebnis:
Der Benutzer ist von der Webanwendung abgemeldet
*/

describe('/TF01-2/', () => {
	it('Abmeldung eines Users', () => {
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

		/* Button mit Username klicken und im Popup auf Logout klicken */
		cy.get('span').contains('Cypress').click()
		cy.get('span').contains('Logout').click()
		cy.wait(500)

		/* Verifizierung, dass man sich im Login-Bereich befindet */
		cy.url().should('contain', '/auth/login')
	})
})

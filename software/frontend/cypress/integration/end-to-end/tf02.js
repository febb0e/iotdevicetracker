/* eslint-disable no-undef */
/*
/TF02/ Registrierung eines Users

Anwendungsfall		[LF02]

Beschreibung
Folgende Schritte sind auszuführen:

1. https://www.iotdevicetracker.software/auth/register aufrufen
2. Username eingeben
3. Email Adresse in validem Format eingeben
4. Passwort >= 8 Zeichen eingeben
5. Eingabe mit identischem Passwort wiederholen
6. Button "Register" klicken
7. Verifikations-Token aus Email eingeben

Erwartetes Ergebnis:
Ein neuer Nutzer wird erfolgreich angelegt und befindet sich beim Loginfenster
*/

/* Eine Random UUID wird dem Username angehangen, 
damit ein unregistrierter Username verwendet wird */
const uuid = () => Cypress._.random(0, 1e6)
const id = uuid()
const testname = 'testname' + id.toString()

describe('/TF02/', () => {
	it('Registrierung eines Users', () => {
		/* IoT Device Tracker Website aufrufen */
		cy.visit('https://www.iotdevicetracker.software')
		cy.get('button').contains('Login').click()
		cy.url().should('include', '/auth/login')

		/* Auf Button "Register" klicken */
		cy.get('a').contains('Register').click()
		cy.url().should('include', '/auth/register')

		/* Username, E-Mail und Passwort eintragen */
		cy.get('input[id=username]').type(testname)
		cy.get('input[id=email]').type(testname + '@test.com')
		cy.get('input[id=password]').type('12345678')
		cy.get('input[id=passwordConfirmation]').type('12345678')

		/* Den User mit Klick auf "Register" registrieren */
		cy.get('span').contains('Register').click({ force: true })
		cy.url().should('include', '/auth/verify')

		/* Die Prüfung der E-Mail Token Validierung entfällt */
	})
})

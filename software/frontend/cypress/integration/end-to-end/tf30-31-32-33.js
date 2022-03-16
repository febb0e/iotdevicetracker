/* eslint-disable no-undef */

/* Eine Random UUID wird der DeviceGroup angehangen, 
um eine neue eindeutige Device-Group zu erzeugen */
const id = getId().toString()

function getId() {
	const uuid = () => Cypress._.random(0, 1e6)
	const id = uuid().toString()
	return id
}

describe('/TF30/TF31/TF32/TF33/', () => {
	it('Nutzer zu Device Group hinzufügen / Nutzer der Device Group anzeigen / Nutzer der Device Group bearbeiten / Nutzer in Device Group entfernen', () => {
		/*
		/TF30/ Nutzer zu Device Group hinzufügen

		Anwendungsfall		[LF30]

		Beschreibung
		Folgende Schritte sind auszuführen:

		1. Device Group auswählen
		2. Unter Users auf den Button 'Add Users' klicken
		3. Im Eingabefeld 'Username' Benutzernamen festlegen
		4. Im Dropdown 'Role' die Rolle 'OBSERVER' oder 'ADMIN' auswählen
		5. Auf Button 'Add User' klicken

		Erwartetes Ergebnis:
		User wird hinzugefügt.
		*/
		cy.visit('https://www.iotdevicetracker.software')
		cy.get('button').contains('Login').click()
		cy.url().should('include', '/auth/login')
		cy.get('input[id="login"]').type('Cypress')
		cy.get('input[id="password"]').type('cypress123')
		cy.get('button').get('span').contains('Sign in').click()
		cy.xpath('/html/body/div/div/div[2]/main/div/div[2]/div/div/p').click()

		/* Für den Test eine neue Device-Group anlegen */
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[1]/div/div/div/div/div/input'
		).type('Device Group' + id)
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[2]/button[1]'
		).click()
		cy.get('p').should('contain', 'Device Group' + id)

		/*
		/TF31/ Nutzer der Device Group anzeigen

		Anwendungsfall		[LF31]

		Beschreibung
		Folgende Schritte sind auszuführen:

		1. Device Group auswählen
		2. 'Users' zeigt alle User an, mindestens den Ersteller der Gruppe selbst

		Erwartetes Ergebnis:
		User der Device-Group sind sichtbar.
		*/
		cy.get('p')
			.contains('Device Group' + id)
			.click()
		cy.get('div').should('contain', 'Create Device ...')
		cy.get('th').should('contain', 'Name')
		cy.get('th').should('contain', 'Email')
		cy.get('th').should('contain', 'Role')
		cy.get('th').should('contain', 'MFA enabled')
		cy.get('td').should('contain', 'Cypress')

		/*
		/TF32/ Nutzer der Device Group bearbeiten

		Anwendungsfall		[LF32]

		Beschreibung
		Folgende Schritte sind auszuführen:

		1. Device Group auswählen
		2. Unter Users einen User aus der Liste auswählen und 'Edit' Icon klicken
		3. Role im Dropdown verändern
		4. Auf Button 'Edit' klicken

		Erwartetes Ergebnis:
		Role wurde angepasst.
		*/
		cy.xpath(
			'/html/body/div/div/div[2]/main/div/div[5]/div/div/div/nav/div/button'
		).click()
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[1]/div/div/div/div[1]/div/input'
		).type('foo')
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[2]/button[1]'
		).click()
		cy.wait(500)
		cy.get('td').should('contain', 'foo')
		cy.get('td').should('contain', 'foo@bar.de')
		cy.get('td').should('contain', 'OBSERVER')
		cy.get('td').should('contain', 'Disabled')
		cy.xpath(
			'/html/body/div/div/div[2]/main/div/div[5]/div/div/div/table/tbody/tr[2]/td[5]/button[1]'
		).click()
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[1]/div/div/div/div/select'
		).select('ADMIN')
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[2]/button[1]'
		).click()
		cy.wait(500)
		cy.xpath(
			'/html/body/div/div/div[2]/main/div/div[5]/div/div/div/table/tbody/tr[2]/td[3]'
		).should('contain', 'ADMIN')

		/*
		/TF33/ Nutzer in Device Group entfernen
		
		Anwendungsfall		[LF33]

		Beschreibung
		Folgende Schritte sind auszuführen:

		1. Device Group auswählen
		2. Unter Users einen User aus der Liste auswählen und 'Delete' Icon klicken
		3. Bestätigen durch Drücken auf 'Revoke access' Button.

		Erwartetes Ergebnis:
		User befindet sich nicht mehr in der Gruppe und hat keinen Zugriff.
		*/
		cy.xpath(
			'/html/body/div/div/div[2]/main/div/div[5]/div/div/div/table/tbody/tr[2]/td[5]/button[2]'
		).click()
		cy.xpath('/html/body/div[2]/div/div/div/div[2]/div[2]/button[1]').click()
		cy.wait(500)
		cy.get('td').should('not.contain', 'foo')
		cy.get('td').should('not.contain', 'foo@bar.de')
		cy.xpath('/html/body/div/div/div[2]/main/div/div[1]/button[2]').click()
		cy.xpath('/html/body/div[2]/div/div/div/div[2]/div[2]/button[1]').click()
		cy.wait(500)
		cy.get('p').should('contain', 'Successfully deleted')
	})
})

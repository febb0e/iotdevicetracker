/* eslint-disable no-undef */

/* Eine Random UUID wird der DeviceGroup angehangen, 
um eine Device-Group mit einem eindeutigen Namen zu bearbeiten */
const id = getId().toString()

function getId() {
	const uuid = () => Cypress._.random(0, 1e6)
	const id = uuid().toString()
	return id
}

describe('/TF20/TF21/TF22/TF23/', () => {
	it('Device Group hinzufügen / Device Group anzeigen / Device Group bearbeiten / Device Group entfernen', () => {
		/*
		/TF20/ Device Group Hinzufügen

		Anwendungsfall		[LF20]

		Beschreibung
		Folgende Schritte sind auszuführen:

		1. Dashboard aufrufen
		2. Auf 'Create Device Group' klicken
		3. Device Group in Eingabefeld 'Name' benennen
		4. Auf den Button 'Create' drücken

		Erwartetes Ergebnis:
		Eine Device Group wird erfolgreich angelegt und wird im Dashboard angezeigt
		*/
		cy.visit('https://www.iotdevicetracker.software')
		cy.get('button').contains('Login').click()
		cy.url().should('include', '/auth/login')
		cy.get('input[id="login"]').type('Cypress')
		cy.get('input[id="password"]').type('cypress123')
		cy.get('button').get('span').contains('Sign in').click()

		/* Auf "Create Device Group" klicken und mit eindeutigem Namen anlegen */
		cy.get('div').contains(' Create Device Group ... ').click()
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[1]/div/div/div/div/div/input'
		).type('Device Group' + id)
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[2]/button[1]'
		).click()

		/*
		/TF21/ Device Group anzeigen

		Anwendungsfall		[LF21]

		Beschreibung
		Folgende Schritte sind auszuführen:

		1. Dashboard aufrufen
		2. Schaltfläche identifizieren die, die zuvor erstellte Device Group repräsentiert
		3. Auf die Schaltfläche drücken

		Erwartetes Ergebnis:
		Die Device Group wird übersichtlich dargestellt mit den zugehörigen Usern sowie Devices.
		*/
		cy.get('p').should('contain', 'Device Group' + id)
		cy.get('p')
			.contains('Device Group' + id)
			.click()
		cy.get('div').should('contain', 'Create Device ...')
		cy.get('th').should('contain', 'Name')
		cy.get('th').should('contain', 'Email')
		cy.get('th').should('contain', 'Role')
		cy.get('th').should('contain', 'MFA enabled')
		cy.get('td').should('contain', 'Cypress')
		cy.get('button').contains('Edit').click()

		/*
		/TF22/ Device Group bearbeiten

		Anwendungsfall		[LF22]

		Beschreibung
		Folgende Schritte sind auszuführen:

		1. Device Group auswählen
		2. Button 'Edit' klicken
		3. Im Eingabefeld 'Name' Namensänderung durchführen
		4. Durch Klick auf 'Edit' bestätigen

		Erwartetes Ergebnis:
		Die Device Group wird übersichtlich dargestellt mit den zugehörigen Usern sowie Devices.
		*/
		const newName = 'Device Group' + getId().toString()
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[1]/div/div/div/div/div/input'
		)
			.clear()
			.type(newName)
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[2]/button[1]'
		).click()
		cy.get('h3').should('contain', newName)

		/*
		/TF23/ Device Group entfernen
		
		Anwendungsfall		[LF23]

		Beschreibung
		Folgende Schritte sind auszuführen:

		1. Device Group auswählen
		2. Button 'Delete' klicken
		3. Im Dialog auf den Button 'Delete' drücken
		
		Erwartetes Ergebnis:
		Device Group wurde gelöscht.
		*/
		cy.xpath('/html/body/div/div/div[2]/main/div/div[1]/button[2]').click()
		cy.xpath('/html/body/div[2]/div/div/div/div[2]/div[2]/button[1]').click()
		cy.wait(500)
		cy.get('p').should('contain', 'Successfully deleted')
		cy.end()
	})
})

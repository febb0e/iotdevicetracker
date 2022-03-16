/* eslint-disable no-undef */

const macAddress = getMac().toString()
const id = getId().toString()

/* Eine Random UUID wird dem Device-Namen angehangen, 
damit ein eindeutiger Device-Name verwendet wird */
function getId() {
	const uuid = () => Cypress._.random(0, 1e6)
	const id = uuid().toString()
	return id
}

/* Eine zufällige MAC-Adresse erzeugen für die
Registrierung eines neuen Devices */
function getMac() {
	var hexDigits = '0123456789ABCDEF'
	var macAddress = ''
	for (var i = 0; i < 6; i++) {
		macAddress += hexDigits.charAt(Math.round(Math.random() * 15))
		macAddress += hexDigits.charAt(Math.round(Math.random() * 15))
		if (i != 5) macAddress += ':'
	}

	return macAddress
}

describe('/TF10/TF11/TF12/TF13/', () => {
	it('Devices hinzufügen / Devices anzeigen / Devices bearbeiten / Devices entfernen', () => {
		/*
		/TF10/ Devices hinzufügen

		Anwendungsfall		[LF10]

		Beschreibung
		Folgende Schritte sind auszuführen:

		1. Der User muss sich innerhalb einer Device Group befinden
		2. Auf "Create Device" klicken
		3. Die MAC-Adresse des Geräts als Identifier eingeben
		4. Einen beliebigen Namen für das Gerät auswählen
		5. Auf "Create" klicken
		6. Den Token des Geräts aufbewahren (entfällt)
		7. Daemon auf dem Device installieren und starten (entfällt)

		Erwartetes Ergebnis:
		Das neue Device wird unter Devices gelistet und kann nach der Installation 
		und dem Start des Daemon Metriken an die Anwendung schicken.
		*/
		cy.visit('https://www.iotdevicetracker.software')
		cy.get('button').contains('Login').click()
		cy.url().should('include', '/auth/login')
		cy.get('input[id="login"]').type('Cypress')
		cy.get('input[id="password"]').type('cypress123')
		cy.get('button').get('span').contains('Sign in').click()
		cy.get('h3').should('contain', 'My Device Groups')
		cy.get('p').contains('Cypress Devices').click()
		cy.get('div').contains(' Create Device ... ').click()
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[1]/div/div/div/div[1]/div/input'
		).type(macAddress)
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[1]/div/div/div/div[2]/div/input'
		).type('Device' + id)
		cy.get('span').contains('Create').click()
		cy.xpath('/html/body/div[2]/div/div/div/div[2]/form/div[2]/button').click({
			force: true,
			multiple: true,
		})

		/*
		/TF11/ Devices anzeigen

		Anwendungsfall		[LF11]

		Beschreibung
		Folgende Schritte sind auszuführen:

		1. Der User muss sich innerhalb einer Device Group befinden
		2. Der User muss ein korrekt angelegtes Device auswählen
		3. Metric des Device auswählen (entfällt)
		4. Passenden Timeframe wählen (entfällt)

		Erwartetes Ergebnis:
		Das Device wird mit korrektem Namen, korrekter MAC und den Metriken angezeigt.
		*/
		cy.get('p').should('contain', 'Device' + id)
		cy.get('p').should('contain', macAddress)

		/*
		/TF12/ Devices bearbeiten

		Anwendungsfall		[LF12]

		Beschreibung
		Folgende Schritte sind auszuführen:

		1. Der User muss sich innerhalb einer Device Group befinden
		2. Das zu bearbeitende Device auswählen
		3. Auf "Edit" klicken
		4. Namen des Devices bearbeiten
		5. Mit Klick auf "Edit" bestätigen

		Erwartetes Ergebnis:
		Das Device wird mit geändertem Namen, korrekter MAC und den Metriken angezeigt.
		*/
		cy.xpath('/html/body/div/div/div[2]/main/div')
			.contains('Device' + id)
			.click({ force: true })
		cy.wait(500)
		cy.url().should('contain', '/devices/')

		/* Einen eindeutigen neuen Namen erstellen */
		const newName = 'Device' + getId().toString()

		cy.get('button').contains('Edit').click()
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[1]/div/div/div/div/div/input'
		)
			.clear()
			.type(newName)
		cy.xpath(
			'/html/body/div[2]/div/div/div/div[2]/form/div[2]/button[1]'
		).click({ force: true })
		cy.get('h3').should('contain', newName)

		/*
		/TF13/ Devices entfernen
		
		Anwendungsfall		[LF13]

		Beschreibung
		Folgende Schritte sind auszuführen:

		1. Der User muss sich innerhalb einer Device Group befinden
		2. Das zu löschende Device auswählen
		3. Auf "Delete" klicken
		4. Das Pop-up durch klicken auf "Delete" bestätigen

		Erwartetes Ergebnis:
		Das Device ist aus der Device-Group entfernt worden.
		*/
		cy.xpath('/html/body/div/div/div[2]/main/div/div[1]/button[2]').click()
		cy.get('span').contains('Delete').click()
		cy.wait(500)

		cy.get('p').contains('Cypress Devices').click()
		cy.get('p').should('not.contain', macAddress)
		cy.end()
	})
})

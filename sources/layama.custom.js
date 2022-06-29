/**
 * Altera a câmera ao clicar no botão do menu
 */
 function refreshCamera() {
    if (abriu) openNav() // Fecha o menu se estiver aberto

    const camera = new URLSearchParams(window.location.search).get('camera')
    let cameraList = getLayamaCameras()
    cameraList.sort(function(x, y) { 
        return x.a == camera ? -1 : y.a == camera ? 1 : 0 
    })
    getLayamaCameras = function() { return cameraList }
}

refreshCamera()

/**
 * Disponibiliza imagens com tamanho um pouco acima do tamanho da janela
 */
getLayamaResolutions = function() { 
    const resolutionList = [1024, 1440, 1680, 2048]
    const windowWidth = window.innerWidth

    const layamaResolutions = new BABYLON.SmartArray(0);
    layamaResolutions.push(1024) // O Babylon sempre seleciona o segundo item do array, então o primeiro é apenas mock
    for (resolution of resolutionList) {
        if (resolution >= windowWidth) {
            layamaResolutions.push(resolution.toString())
            break
        }
    }

    if (layamaResolutions.length < 2) layamaResolutions.push(resolutionList[resolutionList.length - 1])

    return layamaResolutions 
}

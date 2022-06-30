var abriu = false
var imagemDoBotao = document.getElementById("imgButton")
var intervaloAtualizaBotaoMais = setInterval(function () { imagemDoBotao.src = "./menu/img/button_01.gif" }, 5000)
var navegacaoLateral = document.getElementById("sidenav")

function openNav() {
    if (!abriu) {
        navegacaoLateral.classList.add("opened")
        imagemDoBotao.src = "./menu/img/button_02.gif"

        abriu = true
        clearInterval(intervaloAtualizaBotaoMais)
    } else {
        navegacaoLateral.classList.remove("opened")
        imagemDoBotao.src = "./menu/img/button_01.gif"

        abriu = false
    }
}

document.querySelectorAll(".bto-accordion").forEach((item) => {
    item.addEventListener("click", () => {
        const parentElement = item.parentElement
        parentElement.parentElement.querySelectorAll(":scope > .opened").forEach((sibling) => {
            if (sibling == parentElement) return
            sibling.classList.remove("opened")
        })
        parentElement.classList.toggle("opened")
    })
})
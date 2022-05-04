package br.com.william.microservice.loja.service;

import br.com.william.microservice.loja.client.FornecedorClient;
import br.com.william.microservice.loja.controller.CompraController;
import br.com.william.microservice.loja.controller.dto.CompraDTO;
import br.com.william.microservice.loja.controller.dto.InfoFornecedorDTO;
import br.com.william.microservice.loja.controller.dto.InfoPedidoDTO;
import br.com.william.microservice.loja.model.Compra;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CompraService {

    private static final Logger LOG = LoggerFactory.getLogger(CompraService.class);

    @Autowired
    private RestTemplate client;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private FornecedorClient fornecedorClient;

    public Compra realizaCompra(CompraDTO compra) {
        /*ResponseEntity<InfoFornecedorDTO> exchange = client.exchange("http://fornecedor/info/" + compra.getEndereco().getEstado(),
                HttpMethod.GET, null, InfoFornecedorDTO.class);

        discoveryClient.getInstances("fornecedor").stream()
                        .forEach(fornecedor -> {
                            System.out.println("localhost:" + fornecedor.getPort());
                        });*/
        String estado = compra.getEndereco().getEstado();

        LOG.info("buscando informações do fornecedor de {} ", estado);
        InfoFornecedorDTO info = fornecedorClient.getInfoPorEstado(estado);

        LOG.info("realizando um pedido");
        InfoPedidoDTO pedido = fornecedorClient.realizaPedido(compra.getItens());

        // System.out.println(exchange.getBody().getEndereco());
        //System.out.println(info.getEndereco());

        Compra compraSalva = new Compra();
        compraSalva.setPedidoId(pedido.getId());
        compraSalva.setTempoDePreparo(pedido.getTempoDePreparo());
        compraSalva.setEnderecoDestino(info.getEndereco());

        return compraSalva;
    }
}

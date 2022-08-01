import {IRawXmlProvider} from './IRawXmlProvider';

export default class SatXmlCancelarUltimaVendaSat implements IRawXmlProvider {
  xml: string;
  constructor() {
    this.xml =
      '<CFeCanc>' +
      '<infCFe chCanc="novoCFe">' +
      '<ide>' +
      '<CNPJ>16716114000172</CNPJ>' +
      '<signAC>SGR-SAT SISTEMA DE GESTAO E RETAGUARDA DO SAT</signAC>' +
      '<numeroCaixa>001</numeroCaixa>' +
      '</ide>' +
      '<emit/>' +
      '<dest></dest>' +
      '<total/>' +
      '</infCFe>' +
      '</CFeCanc>';
  }

  getXml() {
    return this.xml;
  }
}

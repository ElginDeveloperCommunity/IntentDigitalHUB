// ignore_for_file: constant_identifier_names

part of 'bridge_page.dart';

enum InstallmentMethod {
  FINANCIAMENTO_A_VISTA,
  FINANCIAMENTO_PARCELADO_EMISSOR,
  FINANCIAMENTO_PARCELADO_ESTABELECIMENTO
}

extension InstallmentMethodExtension on InstallmentMethod {
  int get installmentMethodCode {
    switch (this) {
      case InstallmentMethod.FINANCIAMENTO_A_VISTA:
        return 1;
      case InstallmentMethod.FINANCIAMENTO_PARCELADO_EMISSOR:
        return 2;
      case InstallmentMethod.FINANCIAMENTO_PARCELADO_ESTABELECIMENTO:
        return 3;
    }
  }
}

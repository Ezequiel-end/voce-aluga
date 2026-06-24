module "compute" {
  source = "./modules/compute"

  project_name         = var.project_name
  subnet_id            = aws_subnet.api_private.id
  ami_id               = var.ami_id
  api_private_ip       = var.api_private_ip
  api_port             = var.api_port
  ssh_public_key       = file("${path.module}/../ansible/files/voce-aluga.pub")
  security_group_ids   = [aws_security_group.api.id]
}

module "database" {
  source = "./modules/database"

  project_name        = var.project_name
  vpc_id              = aws_vpc.main.id
  subnet_ids          = [aws_subnet.db_private_a.id, aws_subnet.db_private_b.id]
  db_name             = var.db_name
  db_username         = var.db_username
  db_password         = var.db_password
  use_localstack_rds  = var.use_localstack_rds
  emulated_rds_host   = var.emulated_rds_host
  emulated_rds_port   = var.emulated_rds_port
  security_group_ids  = [aws_security_group.rds.id]
}

module "storage" {
  source = "./modules/storage"

  project_name        = var.project_name
  localstack_endpoint = var.localstack_endpoint
}

module "kubernetes" {
  source = "./modules/kubernetes"

  namespace        = var.k8s_namespace
  kind_config_path = "${path.module}/../kubernetes/kind-config.yaml"
  cluster_name     = "voce-aluga"
}

resource "aws_api_gateway_rest_api" "gateway" {
  name = "${var.project_name}-gateway"

  endpoint_configuration {
    types = ["REGIONAL"]
  }

  tags = local.common_tags
}

resource "aws_api_gateway_resource" "api" {
  rest_api_id = aws_api_gateway_rest_api.gateway.id
  parent_id   = aws_api_gateway_rest_api.gateway.root_resource_id
  path_part   = "api"
}

resource "aws_api_gateway_resource" "api_proxy" {
  rest_api_id = aws_api_gateway_rest_api.gateway.id
  parent_id   = aws_api_gateway_resource.api.id
  path_part   = "{proxy+}"
}

resource "aws_api_gateway_method" "api_proxy" {
  rest_api_id   = aws_api_gateway_rest_api.gateway.id
  resource_id   = aws_api_gateway_resource.api_proxy.id
  http_method   = "ANY"
  authorization = "NONE"

  request_parameters = {
    "method.request.path.proxy" = true
  }
}

resource "aws_api_gateway_integration" "api_proxy" {
  rest_api_id = aws_api_gateway_rest_api.gateway.id
  resource_id = aws_api_gateway_resource.api_proxy.id
  http_method = aws_api_gateway_method.api_proxy.http_method

  type                    = "HTTP_PROXY"
  integration_http_method = "ANY"
  uri                     = "http://${module.compute.api_private_ip}:${var.api_port}/api/{proxy}"

  request_parameters = {
    "integration.request.path.proxy" = "method.request.path.proxy"
  }
}

resource "aws_api_gateway_resource" "web_proxy" {
  rest_api_id = aws_api_gateway_rest_api.gateway.id
  parent_id   = aws_api_gateway_rest_api.gateway.root_resource_id
  path_part   = "{proxy+}"
}

resource "aws_api_gateway_method" "web_proxy" {
  rest_api_id   = aws_api_gateway_rest_api.gateway.id
  resource_id   = aws_api_gateway_resource.web_proxy.id
  http_method   = "ANY"
  authorization = "NONE"

  request_parameters = {
    "method.request.path.proxy" = true
  }
}

resource "aws_api_gateway_integration" "web_proxy" {
  rest_api_id = aws_api_gateway_rest_api.gateway.id
  resource_id = aws_api_gateway_resource.web_proxy.id
  http_method = aws_api_gateway_method.web_proxy.http_method

  type                    = "HTTP_PROXY"
  integration_http_method = "ANY"
  uri                     = "http://${var.gateway_host}:${var.gateway_port}/{proxy}"

  request_parameters = {
    "integration.request.path.proxy" = "method.request.path.proxy"
  }
}

resource "aws_api_gateway_deployment" "gateway" {
  rest_api_id = aws_api_gateway_rest_api.gateway.id

  depends_on = [
    aws_api_gateway_integration.api_proxy,
    aws_api_gateway_integration.web_proxy,
  ]

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_stage" "prod" {
  deployment_id = aws_api_gateway_deployment.gateway.id
  rest_api_id   = aws_api_gateway_rest_api.gateway.id
  stage_name    = "prod"
}

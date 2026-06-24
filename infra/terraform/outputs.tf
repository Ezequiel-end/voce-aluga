output "vpc_id" {
  value = aws_vpc.main.id
}

output "api_ec2_private_ip" {
  value = module.compute.api_private_ip
}

output "rds_endpoint" {
  value = module.database.endpoint
}

output "api_gateway_id" {
  value = aws_api_gateway_rest_api.gateway.id
}

output "api_gateway_invoke_url" {
  value = "http://localhost:4566/restapis/${aws_api_gateway_rest_api.gateway.id}/prod/_user_request_"
}

output "k8s_namespace" {
  value = module.kubernetes.namespace
}
